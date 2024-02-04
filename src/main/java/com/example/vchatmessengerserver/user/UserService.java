package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.channel.ChannelService;
import com.example.vchatmessengerserver.exceptions.*;
import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.group.GroupService;
import com.example.vchatmessengerserver.message.Message;
import com.example.vchatmessengerserver.username.NicknameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.vchatmessengerserver.name.NameService.checkName;
import static com.example.vchatmessengerserver.name.NameService.ok;
import static com.example.vchatmessengerserver.password.PasswordService.checkCorrectness;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ChannelService channelService;

    @Autowired
    NicknameService nicknameService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    GroupService groupService;

    public User create(CreateUserDto createUserDto) {
        if (checkName(createUserDto.getName()) != ok) {
            throw new WrongNameException();
        } else if (nicknameService.checkForUser(createUserDto.getNickname()) != ok) {
            throw new WrongNicknameException();
        } else if (checkCorrectness(createUserDto.getPassword()) != ok) {
            throw new WrongPasswordException();
        } else if (createUserDto.getSecretWords().size() != 5) {
            throw new WrongSecretKeysException();
        } else if (createUserDto.getTypeOfImage() != 1 &&
                createUserDto.getTypeOfImage() != 2) {
            throw new WrongDataException();
        } else {
            User user = new User();
            user.setName(createUserDto.getName());
            user.setNickname(createUserDto.getNickname().toLowerCase().strip());
            user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
            user.setImageData(createUserDto.getImageData());
            user.setChats(new ArrayList<>());
            user.setSecretWords(createUserDto.getSecretWords());
            user.setTypeOfImage(createUserDto.getTypeOfImage());
            return userRepository.saveAndFlush(user);
        }
    }

    public User get(String userNickname) {
        userNickname = userNickname.toLowerCase().strip();
        Optional<User> user = userRepository.findByNickname(userNickname);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException();
        }
    }

    public User get(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException();
        }
    }

    public User getBaseInfo(Long userId) {
        User safeUserObject = get(userId);
        safeUserObject.setPassword("");
        safeUserObject.setChats(new ArrayList<>());
        safeUserObject.setSecretWords(new ArrayList<>());
        return safeUserObject;
    }

    public boolean exists(String userNickname) {
        return userRepository.existsByNickname(userNickname.toLowerCase().strip());
    }

    public boolean exists(User user) {
        return userRepository.existsById(user.getId());
    }

    public boolean isMember(User user, Group group) {
        return user.getChats().contains(group);
    }

    public List<Group> getChats(User user) {
        List<Group> chats = new ArrayList<>();
        for (Group chat: user.getChats()) {
            if (groupService.existsChat(chat)) {
                chats.add(chat);
            }
        }
        if (!chats.equals(user.getChats())) {
            user.setChats(chats);
            userRepository.saveAndFlush(user);
        }
        return chats;
    }

    public List<Group> getChatsWithOffset(User user, int limit, int offset) {
        return userRepository.getChatsWithOffset(user.getId(), limit, offset);
    }

    public List<Group> searchChatsWithOffset(User user, String searchedText, int limit, int offset) {
        return userRepository.searchChatsWithOffset(user.getId(), searchedText, limit, offset);
    }

    public int getAmountOfChats(Long userId) {
        return userRepository.getAmountOfChats(userId);
    }

    public void changeName(Long userId, String newName) {
        User user = get(userId);
        if (checkName(newName) == ok) {
            user.setName(newName);
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongNameException();
        }
    }

    public void changeNickname(Long userId, String newNickname) {
        newNickname = newNickname.toLowerCase().strip();
        User user = get(userId);
        if (nicknameService.checkForUser(newNickname) == ok) {
            user.setNickname(newNickname);
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongNicknameException();
        }
    }

    public void changePassword(Long userId, String newPassword) {
        User user = get(userId);
        if (checkCorrectness(newPassword) == ok) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongPasswordException();
        }
    }

    public void changePassword(String userNickname,
                               int a, String a_value,
                               int b, String b_value,
                               int c, String c_value,
                               String newPassword) {
        if (checkSecretWords(userNickname, a, a_value, b, b_value, c, c_value)) {
            if (checkCorrectness(newPassword) == ok) {
                User user = get(userNickname);
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.saveAndFlush(user);
            } else {
                throw new WrongPasswordException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public void changeSecretWords(Long userId, List<String> secretWords) {
        User user = get(userId);
        if (secretWords.size() == 5) {
            user.setSecretWords(secretWords);
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongSecretKeysException();
        }
    }

    public void changeImage(Long userId, String newImageData) {
        User user = get(userId);
        user.setImageData(newImageData);
        userRepository.saveAndFlush(user);
    }

    public void changeTypeOfImage(Long userId, int newTypeOfImage) {
        User user = get(userId);
        if (newTypeOfImage == 1 || newTypeOfImage == 2) {
            user.setTypeOfImage(newTypeOfImage);
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongDataException();
        }
    }

    public boolean canWrite(User user, Group chat) {
        return user.getChats().contains(chat);
    }

    public boolean canEditChat(User user, Group chat) {
        return chat.getOwner().equals(user);
    }

    public boolean canDeleteMessage(User user, Message message) {
        Group chat = message.getMessageChat();
        if (chat.getType() == 1) {
            return groupService.canDeleteMessage(user, message);
        } else {
            return channelService.canDeleteMessage(user, message);
        }
    }

    public boolean canDeleteChat(User user, Group chat) {
        return user.getChats().contains(chat);
    }

    public User addChat(User user, Group newChat) {
        if (!user.getChats().contains(newChat)) {
            user.getChats().add(newChat);
            if (newChat.getType() == 1) {
                groupService.addMember(user, newChat);
            }
            else {
                channelService.addMember(user, channelService.getByParent(newChat));
            }
        }
        return userRepository.saveAndFlush(user);
    }

    public User removeChat(User user, Group chat) {
        if (chat.getOwner().equals(user)) {
            if (chat.getType() == 1) {
                groupService.delete(user, chat);
            } else {
                channelService.delete(user, channelService.getByParent(chat));
            }
        } else {
            if (chat.getType() == 1) {
                groupService.removeMember(user, chat);
            } else {
                channelService.removeMember(user, channelService.getByParent(chat));
            }
            user.getChats().remove(chat);
            return userRepository.saveAndFlush(user);
        }
        user.getChats().remove(chat);
        return user;
    }

    public boolean checkPassword(String userNickname, String verifiablePassword) {
        return passwordEncoder.matches(verifiablePassword, get(userNickname).getPassword());
    }

    public boolean checkSecretWords(String userNickname,
                                   int a, String a_value,
                                   int b, String b_value,
                                   int c, String c_value
    ) {
        User user = get(userNickname);
        return user.getSecretWords().get(a).equals(a_value.strip()) &&
                user.getSecretWords().get(b).equals(b_value.strip()) &&
                user.getSecretWords().get(c).equals(c_value.strip());
    }

    public void delete(User user) {
        List<Group> chatsIds = new ArrayList<>(user.getChats());
        for (Group group: chatsIds) {
            try {
                if (group.getOwner().equals(user)) {
                    groupService.delete(user, group);
                } else {
                    groupService.removeMember(user, group);
                }
            } catch (Exception ignored) {}
        }
        userRepository.deleteById(user.getId());
    }
}
