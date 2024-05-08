package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.channel.ChannelService;
import com.example.vchatmessengerserver.exceptions.*;
import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.group.GroupService;
import com.example.vchatmessengerserver.message.Message;
import com.example.vchatmessengerserver.message.MessageService;
import com.example.vchatmessengerserver.username.NicknameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.vchatmessengerserver.password.PasswordService.checkCorrectness;
import static com.example.vchatmessengerserver.username.NicknameService.ok;

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

    @Autowired
    MessageService messageService;

    public User create(CreateUserDto createUserDto) {
        if (nicknameService.checkForUser(createUserDto.getNickname()) != ok) {
            throw new IncorrectNicknameException();
        } else if (checkCorrectness(createUserDto.getPassword()) != ok) {
            throw new IncorrectPasswordException();
        } else if (createUserDto.getSecretWords().size() != 5) {
            throw new IncorrectSecretKeysException();
        } else if (createUserDto.getTypeOfImage() != 1 &&
                createUserDto.getTypeOfImage() != 2) {
            throw new IncorrectDataException();
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

    public boolean exists(String userNickname) {
        return userRepository.existsByNickname(userNickname.toLowerCase().strip());
    }

    public boolean exists(User user) {
        return userRepository.existsById(user.getId());
    }

    public boolean isMember(User user, Long chatId) {
        user = get(user.getId());
        Group chat = groupService.getById(chatId);
        return user.getChats().contains(chat);
    }

    public List<Group> getChats(User user) {
        user = get(user.getId());
        List<Group> chats = new ArrayList<>();
        for (Group chat: user.getChats()) {
            if (groupService.existsChat(chat.getId())) {
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
        user = get(user.getId());
        return userRepository.getChatsWithOffset(user.getId(), limit, offset);
    }

    public List<Group> searchChatsWithOffset(User user, String searchedText, int limit, int offset) {
        user = get(user.getId());
        return userRepository.searchChatsWithOffset(user.getId(), searchedText, limit, offset);
    }

    public int getAmountOfChats(Long userId) {
        return userRepository.getAmountOfChats(userId);
    }

    public void changeName(Long userId, String newName) {
        User user = get(userId);
        user.setName(newName);
        userRepository.saveAndFlush(user);
    }

    public void changeNickname(Long userId, String newNickname) {
        newNickname = newNickname.toLowerCase().strip();
        User user = get(userId);
        if (nicknameService.checkForUser(newNickname) == ok) {
            user.setNickname(newNickname);
            userRepository.saveAndFlush(user);
        } else {
            throw new IncorrectNicknameException();
        }
    }

    public void changePassword(Long userId, String newPassword) {
        User user = get(userId);
        if (checkCorrectness(newPassword) == ok) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.saveAndFlush(user);
        } else {
            throw new IncorrectPasswordException();
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
                throw new IncorrectPasswordException();
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
            throw new IncorrectSecretKeysException();
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
            throw new IncorrectDataException();
        }
    }

    public boolean canWrite(User user, Long chatId) {
        user = get(user.getId());
        Group chat = groupService.getChatById(chatId);
        return user.getChats().contains(chat);
    }

    public boolean canEditChat(User user, Long chatId) {
        user = get(user.getId());
        Group chat = groupService.getChatById(chatId);
        return chat.getOwner().equals(user);
    }

    public boolean canDeleteMessage(User user, Long messageId) {
        user = get(user.getId());
        Message message = messageService.get(messageId);
        Group chat = groupService.getChatById(message.getMessageChat().getId());
        if (chat.getType() == 1) {
            return groupService.canDeleteMessage(user, message);
        } else {
            return channelService.canDeleteMessage(user, message);
        }
    }

    public boolean canDeleteChat(User user, Long chatId) {
        user = get(user.getId());
        Group chat = groupService.getChatById(chatId);
        return user.getChats().contains(chat);
    }

    public User addChat(User user, Long newChatId) {
        user = get(user.getId());
        Group newChat = groupService.getChatById(newChatId);
        if (!user.getChats().contains(newChat)) {
            user.getChats().add(newChat);
            if (newChat.getType() == 1) {
                groupService.addMember(user, newChat.getId());
            }
            else {
                channelService.addMember(user, newChat.getId());
            }
        }
        return userRepository.saveAndFlush(user);
    }

    public User removeChat(User user, Long chatId) {
        user = get(user.getId());
        Group chat = groupService.getChatById(chatId);
        if (chat.getOwner().equals(user)) {
            if (chat.getType() == 1) {
                groupService.delete(user, chat.getId());
            } else {
                channelService.delete(user, chat.getId());
            }
        } else {
            if (chat.getType() == 1) {
                groupService.removeMember(user, chat.getId());
            } else {
                channelService.removeMember(user, chat.getId());
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
        user = get(user.getId());
        List<Group> chatsIds = new ArrayList<>(user.getChats());
        for (Group group: chatsIds) {
            try {
                if (group.getOwner().equals(user)) {
                    groupService.delete(user, group.getId());
                } else {
                    groupService.removeMember(user, group.getId());
                }
            } catch (Exception ignored) {}
        }
        userRepository.deleteById(user.getId());
    }
}
