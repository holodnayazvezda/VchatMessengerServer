package com.example.vchatmessengerserver.channel;

import com.example.vchatmessengerserver.exceptions.*;
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

import static com.example.vchatmessengerserver.name.NameService.checkName;
import static com.example.vchatmessengerserver.name.NameService.ok;

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

    public Channel create(User owner, CreateChannelDto dto) {
        if (checkName(dto.getName()) != ok) {
            throw new WrongNameException();
        } else if (nicknameService.checkForChannel(dto.getNickname()) != ok) {
            throw new WrongNicknameException();
        }
        List<User> members = new ArrayList<>();
        for (User member: dto.getMembers()) {
            if (!userService.exists(member)) {
                throw new UserNotFoundException();
            }
            if (!members.contains(member)) {
                members.add(member);
            }
        }
        if (!members.contains(owner)) {
            members.add(owner);
        }
        Channel channel = new Channel();
        channel.setName(dto.getName());
        channel.setNickname(dto.getNickname().toLowerCase().strip());
        channel.setType(2);
        channel.setImageData(dto.getImageData());
        channel.setOwner(owner);
        channel.setCreationDate(ZonedDateTime.now());
        channel.setTypeOfImage(dto.getTypeOfImage());
        channel.setMessages(new ArrayList<>());
        channel.setMembers(members);
        channel.setUnreadMessagesCount(dto.getUnreadMessagesCount());
        Channel channelToReturn = channelRepository.saveAndFlush(channel);
        userService.addChat(channelToReturn.getOwner(), channelToReturn);
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
        channel.setUnreadMessagesCount(getUnreadMessagesCountForUser(user, channel));
        return channel;
    }


    public boolean exists(Group channel) {
        return channelRepository.existsById(channel.getId());
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

    public Channel addMember(User user, Channel channel) {
        List<User> members = channel.getMembers();
        if (!members.contains(user)) {
            members.add(user);
            channel.setMembers(members);
            for (Message message: channel.getMessages()) {
                messageService.addReader(user, message);
            }
            return channelRepository.saveAndFlush(channel);
        } else {
            return channel;
        }
    }

    public Channel removeMember(User user, Channel channel) {
        List<Message> messages = new ArrayList<>();
        for (Message message: new ArrayList<>(channel.getMessages())) {
            if (message.getOwner().equals(user)) {
                removeMessage(user, channel, message);
            } else {
                messages.add(message);
            }
        }
        channel.setMessages(messages);
        channel.getMembers().remove(user);
        return channelRepository.saveAndFlush(channel);
    }

    public Channel editName(User user, Channel channel, String newName) {
        if (channel.getOwner().equals(user)) {
            if (checkName(newName) == ok) {
                channel.setName(newName);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new WrongNameException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editNickname(User user, Channel channel, String newNickname) {
        newNickname = newNickname.toLowerCase().strip();
        if (channel.getOwner().equals(user)) {
            if (nicknameService.checkForChannel(newNickname) == ok || channel.getNickname().equals(newNickname)) {
                channel.setNickname(newNickname);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new WrongNicknameException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editTypeOfImage(User user, Channel channel, Integer newTypeOfImage) {
        if (channel.getOwner().equals(user)) {
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                channel.setTypeOfImage(newTypeOfImage);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new WrongDataException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editImage(User user, Channel channel, String imageData) {
        if (channel.getOwner().equals(user)) {
            channel.setImageData(imageData);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editAll(User user, Channel channel, String newName, String newNickname, Integer newTypeOfImage, String newImageData) {
        newNickname = newNickname.toLowerCase().strip();
        if (channel.getOwner().equals(user)) {
            if (checkName(newName) == ok) {
                channel.setName(newName);
            } else {throw new WrongNameException();}
            if (nicknameService.checkForChannel(newNickname) == 200 || channel.getNickname().equals(newNickname)) {
                channel.setNickname(newNickname);
            } else {throw new WrongNicknameException();}
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                channel.setTypeOfImage(newTypeOfImage);
            } else {throw new WrongDataException();}
            channel.setImageData(newImageData);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Channel addMessage(User user, Channel channel, Message message) {
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

    public Channel removeMessage(User user, Channel channel, Message message) {
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
        Group channel = message.getMessageChat();
        return channel.getOwner().equals(user) &&
                channel.getMessages().contains(message);
    }

        public void delete(User user, Channel channel) {
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
    //            for (User member: channel.getMembers()) {
    //                messageGateway.notifyUserAboutChatDeleting(member, channel);
    //            }
            } else {
                throw new NoRightsException();
            }
    }
}
