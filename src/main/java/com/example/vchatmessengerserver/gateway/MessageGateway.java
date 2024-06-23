package com.example.vchatmessengerserver.gateway;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.vchatmessengerserver.files.avatar.Avatar;
import org.springframework.stereotype.Component;

@Component
public class MessageGateway {
    public void notifyUserAboutNewMessage(Long userId, String messageContent, Long chatId, Integer chatType, String chatName, Avatar avatar, String senderName) {
        SocketIOClient client = SessionManager.getSession(userId);
        if (client != null) {
            client.sendEvent("newMessage", userId, messageContent, chatId, chatType, chatName, avatar, senderName);
        }
    }

    public void notifyUserAboutMessageDeleting(Long userId, Long chatId) {
        SocketIOClient client = SessionManager.getSession(userId);
        if (client != null) {
            client.sendEvent("messageDeleted", chatId);
        }
    }

    public void notifyUserAboutChatDeleting(Long userId, Long chatId) {
        SocketIOClient client = SessionManager.getSession(userId);
        if (client != null) {
            client.sendEvent("chatDeleting", chatId);
        }
    }
}

