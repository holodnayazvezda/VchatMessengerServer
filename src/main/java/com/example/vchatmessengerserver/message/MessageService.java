package com.example.vchatmessengerserver.message;

import com.example.vchatmessengerserver.channel.ChannelRepository;
import com.example.vchatmessengerserver.channel.ChannelService;
import com.example.vchatmessengerserver.exceptions.ChatNotFoundException;
import com.example.vchatmessengerserver.exceptions.MessageNotFoundException;
import com.example.vchatmessengerserver.exceptions.NoRightsException;
import com.example.vchatmessengerserver.exceptions.UserNotFoundException;
import com.example.vchatmessengerserver.gateway.MessageGateway;
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

    @Autowired
    MessageGateway messageGateway;

    public Message addReader(User user, Long messageId) {
        Message message = get(messageId);
        user = userService.get(user.getId());
        if (!message.getReaders().contains(user)) {
            message.getReaders().add(user);
            return messageRepository.saveAndFlush(message);
        }
        return message;
    }

    public Message create(User user, CreateMessageDto dto) {
        if (!userService.isMember(user, dto.getMessageChatId())) {
            throw new NoRightsException();
        }
        Group chat = groupService.getChatById(dto.getMessageChatId());
        // создаем сообщение
        Message message = new Message();
        message.setContent(dto.getContent());
        message.setMessageChat(chat);
        message.setCreationDate(ZonedDateTime.now());
        message.setReaders(new ArrayList<>(Collections.singletonList(user)));
        message.setOwner(user);
        Message messageToReturn = messageRepository.saveAndFlush(message);
        if (chat.getType() == 1) {
            groupService.addMessage(user, messageToReturn.getMessageChat().getId(), messageToReturn.getId());
        } else {
            channelService.addMessage(user, messageToReturn.getMessageChat().getId(), messageToReturn.getId());
        }
        for (User member: groupService.getChatById(message.getMessageChat().getId()).getMembers()) {
            messageGateway.notifyUserAboutNewMessage(member.getId(), messageToReturn.getContent(), messageToReturn.getMessageChat().getId(), chat.getType(), chat.getName(), chat.getImageData(), userService.get(messageToReturn.getOwner().getId()).getName());
        }
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

    public List<User> getReaders(User user, Long messageId) {
        Message message = get(messageId);
        List<User> users = new ArrayList<>();
        for (User reader: message.getReaders()) {
            if (!reader.equals(user)) {
                User finalReader;
                try {
                    finalReader = userService.get(reader.getId());
                    users.add(finalReader);
                } catch (UserNotFoundException ignored) {
                }
            }
        }
        return users;
    }

    public List<Message> findMessages(Group chat, String content, int limit, int offset) {
        return messageRepository.findMessagesIds(chat.getId(), content, limit, offset);
    }

    public List<Integer> getPositionsOfFoundMessages(Long chatId, String content, int limit, int offset) {
        Group chat = groupService.getChatById(chatId);
        if (content.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> positions = new ArrayList<>();
        List<Message> chatMessages = groupService.getChatById(chat.getId()).getMessages();
        Collections.reverse(chatMessages);
        for (Message foundMessage: findMessages(chat, content, limit, offset)) {
            if (chatMessages.contains(foundMessage)) {
                positions.add(chatMessages.indexOf(foundMessage));
            }
        }
        return positions;
    }

    public Message getLastMessage(User user, Long chatId) {
        Group chat = groupService.getChatById(chatId);
        try {
            return get(chat.getMessages().get(chat.getMessages().size() - 1).getId());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Message> getMessagesWithOffset(Long chatId, int limit, int offset) {
        Group chat = groupService.getChatById(chatId);
        return messageRepository.getMessagesWithOffset(chat.getId(), limit, offset);
    }

    public void delete(User user, Long messageId) {
        Message message = get(messageId);
        user = userService.get(user.getId());
        Group group = groupService.getById(message.getMessageChat().getId());
        List<Message> messages = group.getMessages();
        if (userService.canDeleteMessage(user, messageId)) {
            messages.remove(message);
            messageRepository.delete(message);
            group.setMessages(messages);
            if (group.getType() == 1) {groupRepository.saveAndFlush(group);}
            else {channelRepository.saveAndFlush(channelService.getByParent(group));}
            for (User member: groupService.getChatById(message.getMessageChat().getId()).getMembers()) {
                messageGateway.notifyUserAboutMessageDeleting(member.getId(), message.getMessageChat().getId());
            }
        } else {
            throw new NoRightsException();
        }
    }
}
