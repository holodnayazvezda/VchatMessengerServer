package com.example.vchatmessengerserver.username;

import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Service;

@Service
public class NicknameService {

    public static int textError = 400;
    public static int uniqueError = 500;
    public static int ok = 200;

    public int checkForUser(String nickname) {
        String regex = "^[a-zA-Z0-9_]+$";  // регулярное выражение для проверки
        if (nickname.matches(regex)) {
            if (nickname.length() >= 5 && nickname.length() <= 30) {
                if (!nickname.matches("[0-9_]+")) {
//                    TODO: Раскомментировать, когда напишу код для пользователей
//                    List<User> data = userRepository.findAll();
//                    List<String> nicknames = new ArrayList<>();
//                    for (User user: data) {nicknames.add(user.getNickname());}
//                    if (!nicknames.contains(nickname)) {
//                        return ok;
//                    } else {
//                        return uniqueError;
//                    }
                    return ok;
                }
            }
        }
        return textError;
    }

    public int checkForChannel(String nickname) {
        String regex = "^[a-zA-Z0-9_]+$";  // регулярное выражение для проверки
        if (nickname.matches(regex)) {
            if (nickname.length() >= 5 && nickname.length() <= 30) {
//                TODO: Раскомментировать, когда напишу код для каналов
//                if (!nickname.matches("[0-9_]+")) {
//                    List<Channel> data = channelRepository.findAll();
//                    List<String> nicknames = new ArrayList<>();
//                    for (Channel channel: data) {nicknames.add(channel.getNickname());}
//                    if (!nicknames.contains(nickname)) {
//                        return ok;
//                    } else {
//                        return uniqueError;
//                    }
//                    return ok;
//                }
                return ok;
            }
        }
        return textError;
    }
}
