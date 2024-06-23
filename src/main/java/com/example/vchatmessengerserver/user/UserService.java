package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.channel.ChannelService;
import com.example.vchatmessengerserver.exceptions.*;
import com.example.vchatmessengerserver.files.avatar.Avatar;
import com.example.vchatmessengerserver.files.avatar.AvatarDTO;
import com.example.vchatmessengerserver.files.avatar.AvatarService;
import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.group.GroupService;
import com.example.vchatmessengerserver.message.Message;
import com.example.vchatmessengerserver.message.MessageService;
import com.example.vchatmessengerserver.username.NicknameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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

    @Autowired
    AvatarService avatarService;

    public List<String> create(CreateUserDTO createUserDto) {
        if (nicknameService.checkForUser(createUserDto.getNickname()) != ok) {
            throw new IncorrectNicknameException();
        } else if (checkCorrectness(createUserDto.getPassword()) != ok) {
            throw new IncorrectPasswordException();
        } else if (createUserDto.getAvatarDTO().getAvatarType() != 1 &&
                   createUserDto.getAvatarDTO().getAvatarType() != 2
        ) {
            throw new IncorrectDataException();
        } else {
            User user = new User();
            user.setName(createUserDto.getName());
            user.setNickname(createUserDto.getNickname().toLowerCase().strip());
            user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));

            Avatar usersAvatar = avatarService.createAvatar(createUserDto.getAvatarDTO());
            user.setAvatar(usersAvatar);

            user.setChats(new ArrayList<>());
            user.setSecretKey(generateSecretKey());
            User savedUser = userRepository.saveAndFlush(user);
            return savedUser.getSecretKey();
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

    public List<String> getSecretKey(User user) {
        user = get(user.getId());
        return user.getSecretKey();
    }

    public List<String> generateSecretKey() {
        // Получаем ресурс (файл) из папки resources/static
        ClassPathResource resource = new ClassPathResource("static/words_list.txt");
        try {
            // Читаем файл с помощью BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            List<String> words = new ArrayList<>();
            String line;

            // Читаем каждую строку файла
            while ((line = reader.readLine()) != null) {
                // Разделяем строку на слова по пробелам
                String[] splitWords = line.split("\\s+");
                // Добавляем слова в список
                Collections.addAll(words, splitWords);
            }

            // Закрываем BufferedReader
            reader.close();

            // Генерируем 5 случайных индексов
            List<String> randomWords = new ArrayList<>();
            Random random = new Random();
            for (int i = 0; i < 5; i++) {
                int randomIndex = random.nextInt(words.size());
                randomWords.add(words.get(randomIndex));
            }

            return randomWords;
        } catch (IOException e) {
            return Arrays.asList("code", "vchat", "data", "security", "programming");
        }
    }

    public List<String> regenerateSecretKey(User user) {
        user = get(user.getId());
        List<String> newSecretKey = generateSecretKey();
        user.setSecretKey(newSecretKey);
        userRepository.saveAndFlush(user);
        return newSecretKey;
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
                               List<Integer> secretKeyWordsNumbers,
                               List<String> secretKeyWords,
                               String newPassword) {
        if (checkSecretKey(userNickname, secretKeyWordsNumbers, secretKeyWords)) {
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

    public void changeAvatar(Long userId, AvatarDTO newAvatarDTO) {
        if (newAvatarDTO.getAvatarType() != 1 && newAvatarDTO.getAvatarType() != 2) {
            throw new IncorrectDataException();
        }
        Avatar newAvatar = avatarService.createAvatar(newAvatarDTO);
        User user = get(userId);
        user.setAvatar(newAvatar);
        userRepository.saveAndFlush(user);
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

    public boolean checkSecretKey(
            String userNickname,
            List<Integer> secretKeyWordsNumbers,
            List<String> secretKeyWords
    ) {
        User user = get(userNickname);
        for (int secretKeyWordNumber: secretKeyWordsNumbers) {
            int secretKeyWordIndex = secretKeyWordsNumbers.indexOf(secretKeyWordNumber);
            if (!user.getSecretKey().get(secretKeyWordNumber).equals(secretKeyWords.get(secretKeyWordIndex))) {
                return false;
            }
        }
        return true;
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
