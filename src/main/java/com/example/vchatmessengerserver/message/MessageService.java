package com.example.vchatmessengerserver.message;

import com.example.vchatmessengerserver.channel.ChannelRepository;
import com.example.vchatmessengerserver.channel.ChannelService;
import com.example.vchatmessengerserver.exceptions.ChatNotFoundException;
import com.example.vchatmessengerserver.exceptions.MessageNotFoundException;
import com.example.vchatmessengerserver.exceptions.NoRightsException;
import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.group.GroupRepository;
import com.example.vchatmessengerserver.group.GroupService;
import com.example.vchatmessengerserver.user.User;
import com.example.vchatmessengerserver.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    @Lazy
    UserService userService;
    @Autowired
    @Lazy
    GroupService groupService;
    @Autowired
    ChannelService channelService;

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ChannelRepository channelRepository;

    public Message addReader(User user, Message message) {
        if (!message.getReaders().contains(user)) {
            message.getReaders().add(user);
            return messageRepository.saveAndFlush(message);
        }
        return message;
    }

    public Message create(User user, CreateMessageDto dto) {
        if (!groupService.existsChat(dto.getMessageChat())) {
            throw new ChatNotFoundException();
        }
        if (!userService.isMember(user, dto.getMessageChat())) {
            throw new NoRightsException();
        }
        // создаем сообщение
        Message message = new Message();
        message.setContent(dto.getContent());
        message.setMessageChat(dto.getMessageChat());
        message.setCreationDate(ZonedDateTime.now());
        message.setReaders(new ArrayList<>(Collections.singletonList(user)));
        message.setOwner(user);
        Message messageToReturn = messageRepository.saveAndFlush(message);
        Group chat = groupService.getChatById(messageToReturn.getMessageChat().getId());
        if (chat.getType() == 1) {
            groupService.addMessage(user, messageToReturn.getMessageChat(), messageToReturn);
        } else {
            channelService.addMessage(user, channelService.getByParent(messageToReturn.getMessageChat()), messageToReturn);
        }
//        for (User member: groupService.getChatById(message.getMessageChat().getId()).getMembers()) {
//            messageGateway.notifyUserAboutNewMessage(member, messageToReturn.getContent(), messageToReturn.getMessageChat(), chat.getType(), chat.getName(), chat.getImageData(), userService.get(messageToReturn.getOwnerId()).getName());
//        }
        return messageToReturn;
    }

    public Message get(Long messageId) {
        Optional<Message> messageObject = messageRepository.findById(messageId);
        if (messageObject.isPresent()) {
            return messageObject.get();
        } else {
            throw new MessageNotFoundException();
        }
    }

    public boolean exists(Message message) {
        return messageRepository.existsById(message.getId());
    }

    public List<User> getReaders(User user, Message message) {
        List<User> users = new ArrayList<>();
        for (User reader: message.getReaders()) {
            users.add(userService.get(reader.getId()));
        }
        return users;
    }

    public List<Message> findMessagesIds(Long chatId, String content, int limit, int offset) {
        return messageRepository.findMessagesIds(chatId, content, limit, offset);
    }

    public List<Integer> getPositionsOfFoundMessages(Long chatId, String content, int limit, int offset) {
        if (content.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> positions = new ArrayList<>();
        List<Message> chatMessages = groupService.getChatById(chatId).getMessages();
        Collections.reverse(chatMessages);
        for (Message foundMessage: findMessagesIds(chatId, content, limit, offset)) {
            if (chatMessages.contains(foundMessage)) {
                positions.add(chatMessages.indexOf(foundMessage));
            }
        }
        return positions;
    }

    public Message getLastMessage(User user, Group chat) {
        try {
            return get(chat.getMessages().get(chat.getMessages().size() - 1).getId());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Message> getMessagesWithOffset(Long chatId, int limit, int offset) {
        return messageRepository.getMessagesWithOffset(chatId, limit, offset);
    }

    public void delete(User user, Message message) {
        Group group = groupService.getById(message.getMessageChat().getId());
        List<Message> messages = group.getMessages();
        if (userService.canDeleteMessage(user, message)) {
            messages.remove(message);
            messageRepository.delete(message);
            group.setMessages(messages);
            if (group.getType() == 1) {groupRepository.saveAndFlush(group);}
            else {channelRepository.saveAndFlush(channelService.getByParent(group));}
//            for (User member: groupService.getChatById(message.getMessageChat().getId()).getMembers()) {
//                messageGateway.notifyUserAboutMessageDeleting(member, message.getMessageChat());
//            }
        } else {
            throw new NoRightsException();
        }
    }
}
