package com.example.vchatmessengerserver.group;

import com.example.vchatmessengerserver.channel.ChannelService;
import com.example.vchatmessengerserver.exceptions.*;
import com.example.vchatmessengerserver.files.avatar.Avatar;
import com.example.vchatmessengerserver.files.avatar.AvatarDTO;
import com.example.vchatmessengerserver.files.avatar.AvatarService;
import com.example.vchatmessengerserver.gateway.MessageGateway;
import com.example.vchatmessengerserver.message.Message;
import com.example.vchatmessengerserver.message.MessageRepository;
import com.example.vchatmessengerserver.message.MessageService;
import com.example.vchatmessengerserver.user.User;
import com.example.vchatmessengerserver.user.UserRepository;
import com.example.vchatmessengerserver.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    @Lazy
    UserService userService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageService messageService;

    @Autowired
    ChannelService channelService;

    @Autowired
    MessageGateway messageGateway;

    @Autowired
    private AvatarService avatarService;

    public Group create(User owner, CreateGroupDto createGroupDto) {
        if (
                createGroupDto.getAvatarDTO().getAvatarType() != 1 &&
                createGroupDto.getAvatarDTO().getAvatarType() != 2
        ) {
            throw new IncorrectDataException();
        }
        if (!userService.exists(owner)) {
            throw new UserNotFoundException();
        }
        List<User> members = new ArrayList<>();
        for (Long memberId: createGroupDto.getMembersIds()) {
            User member = userService.get(memberId);
            if (!members.contains(member)) {
                members.add(member);
            }
        }
        if (!members.contains(owner)) {
            members.add(owner);
        }
        Group group = new Group();
        group.setName(createGroupDto.getName());
        group.setType(1);

        Avatar groupAvatar = avatarService.createAvatar(createGroupDto.getAvatarDTO());
        group.setAvatar(groupAvatar);

        group.setOwner(owner);
        group.setCreationDate(ZonedDateTime.now());
        group.setMembers(members);
        group.setUnreadMessagesCount(createGroupDto.getUnreadMessagesCount());
        Group groupToReturn = groupRepository.saveAndFlush(group);
        userService.addChat(groupToReturn.getOwner(), group.getId());
        return groupToReturn;
    }

    public Group getById(Long groupId) {
        Optional<Group> groupObj = groupRepository.findById(groupId);
        if (groupObj.isPresent()) {
            return groupObj.get();
        } else {
            throw new ChatNotFoundException();
        }
    }

    public Group getChatById(Long chatId) {
        try {
            return getById(chatId);
        } catch (Exception e) {
            try {
                return channelService.getById(chatId);
            } catch (Exception err) {
                throw new ChatNotFoundException();
            }
        }
    }

    public Group getForUser(User user, Group group) {
        group = getById(group.getId());
        group.setUnreadMessagesCount(getUnreadMessagesCountForUser(user, group));
        return group;
    }

    public Group getChatForUser(User user, Long chatId) {
        Group chat = getChatById(chatId);
        if (chat.getType() == 1) {
            return getForUser(user, chat);
        }
        return channelService.getForUser(user, channelService.getByParent(chat));
    }

    public boolean exists(Long chatId) {
        return groupRepository.existsById(chatId);
    }

    public boolean existsChat(Long chatId) {
        return (exists(chatId) || channelService.exists(chatId));
    }

    public Long getUnreadMessagesCountForUser(User user, Group group) {
        long unreadMessagesCount = 0;
        for (int i = group.getMessages().size() - 1; i > 0; i--) {
            try {
                Message message = group.getMessages().get(i);
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

    public List<Group> searchWithOffset(String chatName, int limit, int offset) {
        return groupRepository.searchChatsWithOffset(chatName, limit, offset);
    }

    public Group addMember(User user, Long groupId) {
        Group group = getById(groupId);
        if (userService.exists(user)) {
            List<User> members = group.getMembers();
            if (!members.contains(user)) {
                members.add(user);
                group.setMembers(members);
                for (Message message: group.getMessages()) {
                    messageService.addReader(user, message.getId());
                }
                return groupRepository.saveAndFlush(group);
            } else {
                return group;
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public Group removeMember(User user, Long groupId) {
        Group group = getById(groupId);
        if (userService.exists(user)) {
            List<Message> messages = new ArrayList<>();
            for (Message message: group.getMessages()) {
                if (message.getOwner().equals(user)) {
                    removeMessage(user, group.getId(), message.getId());
                } else {
                    messages.add(message);
                }
            }
            group.setMessages(messages);
            group.getMembers().remove(user);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Group editName(User user, Long groupId, String newName) {
        Group group = getById(groupId);
        if (group.getOwner().equals(user)) {
            group.setName(newName);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new NoRightsException();
        }
    }

    public Group changeAvatar(User user, Long groupId, AvatarDTO newAvatarDTO) {
        Group group = getById(groupId);
        if (group.getOwner().equals(user)) {
            if (newAvatarDTO.getAvatarType() != 1 && newAvatarDTO.getAvatarType() != 2) {
                throw new IncorrectDataException();
            }
            Avatar newAvatar = avatarService.createAvatar(newAvatarDTO);
            group.setAvatar(newAvatar);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new NoRightsException();
        }
    }

    public Group editAll(
            User user,
            Long groupId,
            String newName,
            AvatarDTO newAvatarDTO
    ) {
        Group group = getById(groupId);
        if (!group.getOwner().equals(user)) {
            throw new NoRightsException();
        }
        if (newAvatarDTO.getAvatarType() != 1 && newAvatarDTO.getAvatarType() != 2) {
            throw new IncorrectDataException();
        }
        Avatar newAvatar = avatarService.createAvatar(newAvatarDTO);

        group.setName(newName);
        group.setAvatar(newAvatar);
        return groupRepository.saveAndFlush(group);
    }

    public boolean canDeleteMessage(User user, Message message) {
        Group group = getById(message.getMessageChat().getId());
        return group.getMembers().contains(user) &&
                group.getMessages().contains(message) &&
                (group.getOwner().equals(user) || message.getOwner().equals(user    ));
    }

    public Group addMessage(User user, Long groupId, Long messageId) {
        Group group = getById(groupId);
        Message message = messageService.get(messageId);
        if (group.getMembers().contains(user)) {
            List<Message> messages = group.getMessages();
            if (!messages.contains(message)) {
                messages.add(message);
                group.setMessages(messages);
                return groupRepository.saveAndFlush(group);
            } else {
                return group;
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Group removeMessage(User user, Long groupId, Long messageId) {
        Group group = getById(groupId);
        Message message = messageService.get(messageId);
        List<Message> messages = group.getMessages();
        if (userService.canDeleteMessage(user, messageId)) {
            messages.remove(message);
            messageRepository.deleteById(message.getId());
            group.setMessages(messages);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new NoRightsException();
        }
    }




    public void delete(User user, Long groupId) {
        Group group = getById(groupId);
        if (group.getOwner().equals(user)) {
            for (Message message: group.getMessages()) {
                try {
                    messageRepository.deleteById(message.getId());
                } catch (Exception ignored) {}
            }
            List<User> members = group.getMembers();
            for (User member: members) {
                try {
                    member.getChats().remove(group);
                    userRepository.saveAndFlush(member);
                } catch (Exception ignored) {}
            }
            groupRepository.deleteById(group.getId());
            for (User member: group.getMembers()) {
                messageGateway.notifyUserAboutChatDeleting(member.getId(), groupId);
            }
        } else {
            throw new NoRightsException();
        }
    }
}
