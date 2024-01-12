package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.exceptions.*;
import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.username.NicknameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static com.example.vchatmessengerserver.name.NameService.checkName;
import static com.example.vchatmessengerserver.name.NameService.ok;
import static com.example.vchatmessengerserver.password.PasswordService.checkCorrectness;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    NicknameService nicknameService;

    @Autowired
    PasswordEncoder passwordEncoder;

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

    public boolean exists(Long userId) {
        return userRepository.existsById(userId);
    }

    public boolean isMember(Long userId, Group group) {
        return get(userId).getChats().contains(group);
    }

    // TODO getChatsIds

    public int getAmountOfChats(Long userId) {
        return userRepository.getAmountOfChats(userId);
    }

    // TODO: getChats

    // TODO: getChatsIdsWithOffset

    // TODO: getChatsWithOffset

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
}
