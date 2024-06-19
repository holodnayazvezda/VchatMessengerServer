package com.example.vchatmessengerserver.user;


import com.example.vchatmessengerserver.auth.Auth;
import com.example.vchatmessengerserver.group.Group;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PutMapping(value = "/change_name")
    @SecurityRequirement(name = "basicAuth")
    public void changeName(Authentication authentication, String newName) {
        userService.changeName(Auth.getUser(authentication).getId(), newName);
    }

    @PutMapping(value = "/change_nickname")
    @SecurityRequirement(name = "basicAuth")
    public void changeNickname(Authentication authentication, String newNickname) {
        userService.changeNickname(Auth.getUser(authentication).getId(), newNickname);
    }

    @PutMapping(value = "/change_password")
    @SecurityRequirement(name = "basicAuth")
    public void changePassword(Authentication authentication, String password) {
        userService.changePassword(Auth.getUser(authentication).getId(), password);
    }

    @PutMapping(value = "/change_password_by_secret_key")
    public void changePasswordBySecretKey(String userNickname,
                                          @RequestParam List<Integer> secretKeyWordsNumbers,
                                          @RequestParam List<String> secretKeyWords,
                                          String newPassword) {
        userService.changePassword(
                userNickname,
                secretKeyWordsNumbers,
                secretKeyWords,
                newPassword
        );
    }

    @PutMapping(value = "/change_secret_key")
    @SecurityRequirement(name = "basicAuth")
    public void changeSecretKey(Authentication authentication, @RequestParam List<String> secretKey) {
        userService.changeSecretKey(Auth.getUser(authentication).getId(),
                secretKey);
    }

    @PutMapping(value = "/change_image")
    @SecurityRequirement(name = "basicAuth")
    public void changeImage(Authentication authentication, String newImageData) {
        userService.changeImage(Auth.getUser(authentication).getId(),
                newImageData);
    }

    @PutMapping(value = "/set_type_of_image")
    @SecurityRequirement(name = "basicAuth")
    public void setTypeOfImage(Authentication authentication, int newTypeOfImage) {
        userService.changeTypeOfImage(Auth.getUser(authentication).getId(), newTypeOfImage);
    }

    @PutMapping(value = "/add_chat", name = "Add chat with transferred id to list of user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> addChat(Authentication authentication, Long newChatId) {
        return ResponseEntity.ok(
                userService.addChat(Auth.getUser(authentication), newChatId)
        );
    }

    @PutMapping(value = "/remove_chat", name = "Remove chat with transferred id from list of user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> removeChat(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                userService.removeChat(Auth.getUser(authentication), chatId)
        );
    }

    @PostMapping(value = "/create")
    public ResponseEntity<User> create(@RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.ok(
                userService.create(createUserDto)
        );
    }

    @PostMapping(value = "/can_write", name = "Check if user can write to chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canWrite(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                userService.canWrite(Auth.getUser(authentication), chatId)
        );
    }

    @PostMapping(value = "/can_edit_chat", name = "Check if user can edit chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canEditChat(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                userService.canEditChat(Auth.getUser(authentication), chatId)
        );
    }

    @PostMapping(value = "/can_delete_message", name = "Check if user can delete message with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canDeleteMessage(Authentication authentication, Long messageId) {
        return ResponseEntity.ok(
                userService.canDeleteMessage(Auth.getUser(authentication), messageId)
        );
    }

    @PostMapping(value = "/can_delete_chat", name = "Check if user can delete chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canDeleteChat(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                userService.canDeleteChat(Auth.getUser(authentication), chatId)
        );
    }

    @GetMapping(value = "/get")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> get(Authentication authentication) {
        return ResponseEntity.ok(
                userService.get(Auth.getUser(authentication).getNickname())
        );
    }

    @GetMapping(value = "/exists")
    public ResponseEntity<Boolean> exists(String userNickname) {
        return ResponseEntity.ok(
                userService.exists(userNickname)
        );
    }

    @GetMapping(value = "/get_amount_chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Integer> getAmountOfChats(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getAmountOfChats(Auth.getUser(authentication).getId())
        );
    }

    @GetMapping(value = "/get_chats", name = "Get user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Group>> getChats(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getChats(Auth.getUser(authentication))
        );
    }

    @GetMapping(value = "/get_chats_with_offset", name = "Get user's chats with offset")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Group>> getChatsWithOffset(Authentication authentication, int limit, int offset) {
        return ResponseEntity.ok(
                userService.getChatsWithOffset(Auth.getUser(authentication), limit, offset)
        );
    }

    @GetMapping(value = "/generate_secret_key", name = "Generate new secret key")
    public ResponseEntity<List<String>> generateSecretKey() {
        return ResponseEntity.ok(
                userService.generateSecretKey()
        );
    }

    @GetMapping(value = "/search_chats_with_offset", name = "Search user's chats with offset")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Group>> searchChatsWithOffset(Authentication authentication, String searchedText,  int limit, int offset) {
        return ResponseEntity.ok(
                userService.searchChatsWithOffset(Auth.getUser(authentication), searchedText, limit, offset)
        );
    }

    @GetMapping(value = "/check_password")
    public ResponseEntity<Boolean> checkPassword(String userNickname, String verifiablePassword) {
        return ResponseEntity.ok(
                userService.checkPassword(userNickname, verifiablePassword)
        );
    }

    @GetMapping(value = "/check_secret_key")
    public ResponseEntity<Boolean> checkSecretKey(
            String userNickname,
            @RequestParam List<Integer> secretKeyWordsNumbers,
            @RequestParam List<String> secretKeyWords
    ) {
        return ResponseEntity.ok(
                userService.checkSecretKey(
                        userNickname,
                        secretKeyWordsNumbers,
                        secretKeyWords
                )
        );
    }

    @DeleteMapping(value = "/delete")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication) {
        userService.delete(Auth.getUser(authentication));
    }
}
