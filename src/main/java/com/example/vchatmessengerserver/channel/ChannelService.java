package com.example.vchatmessengerserver.channel;

import com.example.vchatmessengerserver.exceptions.*;
import com.example.vchatmessengerserver.gateway.MessageGateway;
import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.message.Message;
import com.example.vchatmessengerserver.message.MessageRepository;
import com.example.vchatmessengerserver.message.MessageService;
import com.example.vchatmessengerserver.user.User;
import com.example.vchatmessengerserver.user.UserRepository;
import com.example.vchatmessengerserver.user.UserService;
import com.example.vchatmessengerserver.username.NicknameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.vchatmessengerserver.username.NicknameService.ok;

@Service
public class ChannelService {
    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    NicknameService nicknameService;

    @Autowired
    @Lazy
    UserService userService;

    @Autowired
    @Lazy
    MessageService messageService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageGateway messageGateway;

    public Channel create(User owner, CreateChannelDto createChannelDto) {
        if (nicknameService.checkForChannel(createChannelDto.getNickname()) != ok) {
            throw new IncorrectNicknameException();
        }
        if (!userService.exists(owner)) {
            throw new UserNotFoundException();
        }
        List<User> members = new ArrayList<>();
        for (Long memberId: createChannelDto.getMembersIds()) {
            User member = userService.get(memberId);
            if (!members.contains(member)) {
                members.add(member);
            }
        }
        if (!members.contains(owner)) {
            members.add(owner);
        }
        if (createChannelDto.getTypeOfImage() != 1 && createChannelDto.getTypeOfImage() != 2) {
            throw new IncorrectDataException();
        }
        Channel channel = new Channel();
        channel.setName(createChannelDto.getName());
        channel.setNickname(createChannelDto.getNickname().toLowerCase().strip());
        channel.setType(2);
        channel.setImageData(createChannelDto.getImageData());
        channel.setOwner(owner);
        channel.setCreationDate(ZonedDateTime.now());
        channel.setTypeOfImage(createChannelDto.getTypeOfImage());
        channel.setMessages(new ArrayList<>());
        channel.setMembers(members);
        channel.setUnreadMessagesCount(createChannelDto.getUnreadMessagesCount());
        Channel channelToReturn = channelRepository.saveAndFlush(channel);
        userService.addChat(channelToReturn.getOwner(), channelToReturn.getId());
        return channelToReturn;
    }

    public Channel getById(Long channelId) {
        Optional<Channel> channelObject = channelRepository.findById(channelId);
        if (channelObject.isPresent()) {
            return channelObject.get();
        } else {
            throw new ChatNotFoundException();
        }
    }

    public Channel getByParent(Group group) {
        Optional<Channel> channelObject = channelRepository.findById(group.getId());
        if (channelObject.isPresent()) {
            Channel channel = channelObject.get();
            channel.setName(group.getName());
            channel.setTypeOfImage(group.getTypeOfImage());
            channel.setImageData(group.getImageData());
            channel.setOwner(group.getOwner());
            channel.setMessages(group.getMessages());
            channel.setMembers(group.getMembers());
            channel.setUnreadMessagesCount(group.getUnreadMessagesCount());
            return channel;
        } else {
            throw new ChatNotFoundException();
        }
    }

    public Channel getForUser(User user, Channel channel) {
        channel = getById(channel.getId());
        channel.setUnreadMessagesCount(getUnreadMessagesCountForUser(user, channel));
        return channel;
    }


    public boolean exists(Long channelId) {
        return channelRepository.existsById(channelId);
    }

    public Long getUnreadMessagesCountForUser(User user, Channel channel) {
        long unreadMessagesCount = 0;
        for (int i = channel.getMessages().size() - 1; i > 0; i--) {
            try {
                Message message = channel.getMessages().get(i);
                if (message.getReaders().contains(user)) {
                    break;
                } else {
                    unreadMessagesCount ++;
                }
            } catch (Exception e) {
                break;
            }
        }
        return unreadMessagesCount;
    }

    public Channel addMember(User user, Long channelId) {
        Channel channel = getById(channelId);
        List<User> members = channel.getMembers();
        if (!members.contains(user)) {
            members.add(user);
            channel.setMembers(members);
            for (Message message: channel.getMessages()) {
                messageService.addReader(user, message.getId());
            }
            return channelRepository.saveAndFlush(channel);
        } else {
            return channel;
        }
    }

    public Channel removeMember(User user, Long channelId) {
        Channel channel = getById(channelId);
        List<Message> messages = new ArrayList<>();
        for (Message message: new ArrayList<>(channel.getMessages())) {
            if (message.getOwner().equals(user)) {
                removeMessage(user, channel.getId(), message.getId());
            } else {
                messages.add(message);
            }
        }
        channel.setMessages(messages);
        channel.getMembers().remove(user);
        return channelRepository.saveAndFlush(channel);
    }

    public Channel editName(User user, Long channelId, String newName) {
        Channel channel = getById(channelId);
        if (channel.getOwner().equals(user)) {
            channel.setName(newName);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editNickname(User user, Long channelId, String newNickname) {
        Channel channel = getById(channelId);
        newNickname = newNickname.toLowerCase().strip();
        if (channel.getOwner().equals(user)) {
            if (nicknameService.checkForChannel(newNickname) == ok || channel.getNickname().equals(newNickname)) {
                channel.setNickname(newNickname);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new IncorrectNicknameException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editTypeOfImage(User user, Long channelId, Integer newTypeOfImage) {
        Channel channel = getById(channelId);
        if (channel.getOwner().equals(user)) {
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                channel.setTypeOfImage(newTypeOfImage);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new IncorrectDataException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editImage(User user, Long channelId, String imageData) {
        Channel channel = getById(channelId);
        if (channel.getOwner().equals(user)) {
            channel.setImageData(imageData);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editAll(User user, Long channelId, String newName, String newNickname, Integer newTypeOfImage, String newImageData) {
        Channel channel = getById(channelId);
        newNickname = newNickname.toLowerCase().strip();
        if (channel.getOwner().equals(user)) {
            channel.setName(newName);
            if (nicknameService.checkForChannel(newNickname) == 200 || channel.getNickname().equals(newNickname)) {
                channel.setNickname(newNickname);
            } else {throw new IncorrectNicknameException();}
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                channel.setTypeOfImage(newTypeOfImage);
            } else {throw new IncorrectDataException();}
            channel.setImageData(newImageData);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Channel addMessage(User user, Long channelId, Long messageId) {
        Channel channel = getById(channelId);
        Message message = messageService.get(messageId);
        if (channel.getOwner().equals(user)) {
            List<Message> messages = channel.getMessages();
            if (!messages.contains(message)) {
                messages.add(message);
                channel.setMessages(messages);
                return channelRepository.saveAndFlush(channel);
            } else {
                return channel;
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel removeMessage(User user, Long channelId, Long messageId) {
        Channel channel = getById(channelId);
        Message message = messageService.get(messageId);
        List<Message> messages = channel.getMessages();
        if (canDeleteMessage(user, message)) {
            messages.remove(message);
            messageRepository.delete(message);
            channel.setMessages(messages);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Boolean canDeleteMessage(User user, Message message) {
        Group channel = getById(message.getMessageChat().getId());
        return channel.getOwner().equals(user) &&
                channel.getMessages().contains(message);
    }

    public void delete(User user, Long channelId) {
        Channel channel = getById(channelId);
        if (channel.getOwner().equals(user)) {
            for (Message message: channel.getMessages()) {
                try {
                    messageRepository.delete(message);
                } catch (Exception ignored) {}
            }
            List<User> members = channel.getMembers();
            for (User member: members) {
                try {
                    member.getChats().remove(channel);
                    userRepository.saveAndFlush(member);
                } catch (Exception ignored) {}
            }
            channelRepository.delete(channel);
            for (User member: channel.getMembers()) {
                messageGateway.notifyUserAboutChatDeleting(member.getId(), channel.getId());
            }
        } else {
            throw new NoRightsException();
        }
    }
}
