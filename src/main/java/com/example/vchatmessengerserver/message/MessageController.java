package com.example.vchatmessengerserver.message;

import com.example.vchatmessengerserver.auth.Auth;
import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.user.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    @PutMapping(value = "/add_reader", name = "Add reader of the message")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Message> addReader(Authentication authentication, Long messageId) {
        return ResponseEntity.ok(
                messageService.addReader(Auth.getUser(authentication), messageId)
        );
    }

    @PostMapping(value = "/create", name = "Create new message")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Message> create(Authentication authentication, @RequestBody CreateMessageDto dto) {
        return ResponseEntity.ok(
                messageService.create(Auth.getUser(authentication), dto)
        );
    }

    @PostMapping(value = "/get", name = "Get message with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Message> get(Long messageId) {
        return ResponseEntity.ok(
                messageService.get(messageId)
        );
    }

    @PostMapping(value = "/get_readers", name = "Get readers of this message")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<User>> getReaders(Authentication authentication, Long messageId) {
        return ResponseEntity.ok(
                messageService.getReaders(Auth.getUser(authentication), messageId)
        );
    }

    @PostMapping(value = "/get_positions_of_found_messages", name = "Search messages in the chat with transferred id and return their positions in list")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Integer>> getPositionsOfFoundMessages(Long chatId, String content, int limit, int offset) {
        return ResponseEntity.ok(
                messageService.getPositionsOfFoundMessages(chatId, content, limit, offset)
        );
    }

    @PostMapping(value = "/get_last_message", name = "Get last message of the chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Message> getLastMessage(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                messageService.getLastMessage(Auth.getUser(authentication), chatId)
        );
    }

    @PostMapping(value = "/get_messages_with_offset", name = "Get all messages of the chat with transferred id with offset")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Message>> getMessagesWithOffset(Long chatId, int limit, int offset) {
        return ResponseEntity.ok(
                messageService.getMessagesWithOffset(chatId, limit, offset)
        );
    }

    @DeleteMapping(value = "/delete", name = "Delete the message with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication, Long messageId) {
        messageService.delete(Auth.getUser(authentication), messageId);
    }
}
