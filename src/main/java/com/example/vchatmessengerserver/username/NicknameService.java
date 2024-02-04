package com.example.vchatmessengerserver.username;

import com.example.vchatmessengerserver.channel.Channel;
import com.example.vchatmessengerserver.channel.ChannelRepository;
import com.example.vchatmessengerserver.user.User;
import com.example.vchatmessengerserver.user.UserRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NicknameService {

    public static int textError = 400;
    public static int uniqueError = 500;
    public static int ok = 200;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ChannelRepository channelRepository;

    public int checkForUser(String nickname) {
        String regex = "^[a-zA-Z0-9_]+$";  // регулярное выражение для проверки
        if (nickname.matches(regex)) {
            if (nickname.length() >= 5 && nickname.length() <= 30) {
                if (!nickname.matches("[0-9_]+")) {
                    List<User> data = userRepository.findAll();
                    List<String> nicknames = new ArrayList<>();
                    for (User user: data) {nicknames.add(user.getNickname());}
                    if (!nicknames.contains(nickname)) {
                        return ok;
                    } else {
                        return uniqueError;
                    }
                }
            }
        }
        return textError;
    }

    public int checkForChannel(String nickname) {
        String regex = "^[a-zA-Z0-9_]+$";  // регулярное выражение для проверки
        if (nickname.matches(regex)) {
            if (nickname.length() >= 5 && nickname.length() <= 30) {
                if (!nickname.matches("[0-9_]+")) {
                    List<Channel> data = channelRepository.findAll();
                    List<String> nicknames = new ArrayList<>();
                    for (Channel channel : data) {
                        nicknames.add(channel.getNickname());
                    }
                    if (!nicknames.contains(nickname)) {
                        return ok;
                    } else {
                        return uniqueError;
                    }
                }
            }
        }
        return textError;
    }
}
