package com.example.vchatmessengerserver.channel;

import com.example.vchatmessengerserver.exceptions.*;
import com.example.vchatmessengerserver.files.avatar.Avatar;
import com.example.vchatmessengerserver.files.avatar.AvatarDTO;
import com.example.vchatmessengerserver.files.avatar.AvatarService;
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

    @Autowired
    private AvatarService avatarService;

    public Channel create(User owner, CreateChannelDto createChannelDto) {
        if (nicknameService.checkForChannel(createChannelDto.getNickname()) != ok) {
            throw new IncorrectNicknameException();
        }
        if (
                createChannelDto.getAvatarDTO().getAvatarType() != 1 &&
                createChannelDto.getAvatarDTO().getAvatarType() != 2
        ) {
            throw new IncorrectDataException();
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
        Channel channel = new Channel();
        channel.setName(createChannelDto.getName());
        channel.setNickname(createChannelDto.getNickname().toLowerCase().strip());
        channel.setType(2);

        Avatar channelAvatar = avatarService.createAvatar(createChannelDto.getAvatarDTO());
        channel.setAvatar(channelAvatar);

        channel.setOwner(owner);
        channel.setCreationDate(ZonedDateTime.now());
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
            channel.setAvatar(group.getAvatar());
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

    public Channel editAvatar(User user, Long channelId, AvatarDTO newAvatarDTO) {
        Channel channel = getById(channelId);
        if (channel.getOwner().equals(user)) {
            if (newAvatarDTO.getAvatarType() != 1 && newAvatarDTO.getAvatarType() != 2) {
                throw new IncorrectDataException();
            }
            Avatar newAvatar = avatarService.createAvatar(newAvatarDTO);
            channel.setAvatar(newAvatar);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editAll(
            User user,
            Long channelId,
            String newName,
            String newNickname,
            AvatarDTO newAvatarDTO
    ) {
        Channel channel = getById(channelId);
        if (!channel.getOwner().equals(user)) {
            throw new NoRightsException();
        }
        newNickname = newNickname.toLowerCase().strip();
        if (nicknameService.checkForChannel(newNickname) != 200 && !channel.getNickname().equals(newNickname)) {
            throw new IncorrectNicknameException();
        }
        channel.setName(newName);
        channel.setNickname(newNickname);
        if (newAvatarDTO.getAvatarType() != 1 && newAvatarDTO.getAvatarType() != 2) {
            throw new IncorrectDataException();
        }
        Avatar newAvatar = avatarService.createAvatar(newAvatarDTO);
        channel.setAvatar(newAvatar);
        return channelRepository.saveAndFlush(channel);
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
