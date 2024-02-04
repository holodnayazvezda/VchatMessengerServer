package com.example.vchatmessengerserver.group;

import com.example.vchatmessengerserver.channel.ChannelService;
import com.example.vchatmessengerserver.exceptions.*;
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

import static com.example.vchatmessengerserver.name.NameService.checkName;
import static com.example.vchatmessengerserver.name.NameService.ok;

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

    public Group create(User owner, CreateGroupDto createGroupDto) {
        if (checkName(createGroupDto.getName()) != ok) {
            throw new WrongNameException();
        }
        List<User> members = new ArrayList<>();
        for (User member: createGroupDto.getMembers()) {
            if (!userService.exists(owner)) {
                throw new UserNotFoundException();
            }
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
        group.setImageData(createGroupDto.getImageData());
        group.setOwner(owner);
        group.setCreationDate(ZonedDateTime.now());
        group.setTypeOfImage(createGroupDto.getTypeOfImage());
        group.setMembers(members);
        group.setUnreadMessagesCount(createGroupDto.getUnreadMessagesCount());
        Group groupToReturn = groupRepository.saveAndFlush(group);
        userService.addChat(groupToReturn.getOwner(), group);
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
        if (exists(group)) {
            group.setUnreadMessagesCount(getUnreadMessagesCountForUser(user, group));
            return group;
        } else {
            throw new ChatNotFoundException();
        }
    }

    public Group getChatForUser(User user, Group chat) {
        if (chat.getType() == 1) {
            return getForUser(user, chat);
        }
        return channelService.getForUser(user, channelService.getByParent(chat));
    }

    public boolean exists(Group group) {
        return groupRepository.existsById(group.getId());
    }

    public boolean existsChat(Group chat) {
        return (exists(chat) || channelService.exists(chat));
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

    public Group addMember(User user, Group group) {
        if (userService.exists(user)) {
            List<User> members = group.getMembers();
            if (!members.contains(user)) {
                members.add(user);
                group.setMembers(members);
                for (Message message: group.getMessages()) {
                    messageService.addReader(user, message);
                }
                return groupRepository.saveAndFlush(group);
            } else {
                return group;
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public Group removeMember(User user, Group group) {
        if (userService.exists(user)) {
            List<Message> messages = new ArrayList<>();
            for (Message message: group.getMessages()) {
                if (message.getOwner().equals(user)) {
                    removeMessage(user, group, message);
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

    public Group editName(User user, Group group, String newName) {
        if (group.getOwner().equals(user)) {
            if (checkName(newName) == ok) {
                group.setName(newName);
                return groupRepository.saveAndFlush(group);
            } else {
                throw new WrongNameException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Group editTypeOfImage(User user, Group group, Integer newTypeOfImage) {
        if (group.getOwner().equals(user)) {
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                group.setTypeOfImage(newTypeOfImage);
                return groupRepository.saveAndFlush(group);
            } else {
                throw new WrongDataException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Group editImage(User user, Group group, String imageData) {
        if (group.getOwner().equals(user)) {
            group.setImageData(imageData);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new NoRightsException();
        }
    }

    public Group editAll(User user, Group group, String newName, Integer newTypeOfImage, String newImageData) {
        if (group.getOwner().equals(user)) {
            if (checkName(newName) == ok) {
                group.setName(newName);
            } else {throw new WrongNameException();}
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                group.setTypeOfImage(newTypeOfImage);
            } else {throw new WrongDataException();}
            group.setImageData(newImageData);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new NoRightsException();
        }
    }

    public boolean canDeleteMessage(User user, Message message) {
        Group group = message.getMessageChat();
        return group.getMembers().contains(user) &&
                group.getMessages().contains(message) &&
                (group.getOwner().equals(user) || message.getOwner().equals(user    ));
    }

    public Group addMessage(User user, Group group, Message message) {
        group = getById(group.getId());
        if (group.getMembers().contains(user)) {
            List<Message> messages = group.getMessages();
            if (!messages.contains(message)) {
                messages.add(message);
                group.setMessages(messages);
                Group g = groupRepository.saveAndFlush(group);
                return g;
            } else {
                return group;
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Group removeMessage(User user, Group group, Message message) {
        if (messageService.exists(message)) {
            List<Message> messages = group.getMessages();
            if (userService.canDeleteMessage(user, message)) {
                messages.remove(message);
                messageRepository.deleteById(message.getId());
                group.setMessages(messages);
                return groupRepository.saveAndFlush(group);
            } else {
                throw new NoRightsException();
            }
        } else {
            throw new MessageNotFoundException();
        }
    }




    public void delete(User user, Group group) {
        if (group.getOwner().equals(user)) {
            for (Message message: group.getMessages()) {
                try {
                    messageRepository.deleteById(message.getId());
                } catch (Exception ignored) {}
            }
            List<User> members = group.getMembers();
            for (User member: members) {
                try {
                    user.getChats().remove(group);
                    userRepository.saveAndFlush(user);
                } catch (Exception ignored) {}
            }
            groupRepository.deleteById(group.getId());
//            for (User member: group.getMembers()) {
//                messageGateway.notifyUserAboutChatDeleting(memberId, groupId);
//            }
        } else {
            throw new NoRightsException();
        }
    }
}
