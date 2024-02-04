package com.example.vchatmessengerserver.user;


import com.example.vchatmessengerserver.auth.Auth;
import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.message.Message;
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

    @PutMapping(value = "/change_password_by_secret_words")
    public void changePasswordBySecretWords(String userNickname,
                                           int a, String a_value,
                                           int b, String b_value,
                                           int c, String c_value,
                                           String newPassword) {
        userService.changePassword(userNickname,
                a, a_value,
                b, b_value,
                c, c_value,
                newPassword
        );
    }

    @PutMapping(value = "/change_secret_words")
    @SecurityRequirement(name = "basicAuth")
    public void changeSecretWords(Authentication authentication, @RequestParam List<String> secretWords) {
        userService.changeSecretWords(Auth.getUser(authentication).getId(),
                secretWords);
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

    @PutMapping(value = "/v1.0/user/add_chat", name = "Add chat with transferred id to list of user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> addChat(Authentication authentication, Group newChat) {
        return ResponseEntity.ok(
                userService.addChat(Auth.getUser(authentication), newChat)
        );
    }

    @PutMapping(value = "/v1.0/user/remove_chat", name = "Remove chat with transferred id from list of user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> removeChat(Authentication authentication, Group chat) {
        return ResponseEntity.ok(
                userService.removeChat(Auth.getUser(authentication), chat)
        );
    }


    @PostMapping(value = "/create")
    public ResponseEntity<User> create(@RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.ok(
                userService.create(createUserDto)
        );
    }

    @GetMapping(value = "/get")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> get(Authentication authentication) {
        return ResponseEntity.ok(
                userService.get(Auth.getUser(authentication).getNickname())
        );
    }

    @GetMapping(value = "/get_base_info")
    public ResponseEntity<User> getBaseInfo(Long userId) {
        return ResponseEntity.ok(
                userService.getBaseInfo(userId)
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

    @GetMapping(value = "/search_chats_with_offset", name = "Search user's chats with offset")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Group>> searchChatsWithOffset(Authentication authentication, String searchedText,  int limit, int offset) {
        return ResponseEntity.ok(
                userService.searchChatsWithOffset(Auth.getUser(authentication), searchedText, limit, offset)
        );
    }

    @GetMapping(value = "/v1.0/user/can_write", name = "Check if user can write to chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canWrite(Authentication authentication, Group chat) {
        return ResponseEntity.ok(
                userService.canWrite(Auth.getUser(authentication), chat)
        );
    }

    @GetMapping(value = "/v1.0/user/can_edit_chat", name = "Check if user can edit chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canEditChat(Authentication authentication, Group chat) {
        return ResponseEntity.ok(
                userService.canEditChat(Auth.getUser(authentication), chat)
        );
    }

    @GetMapping(value = "/v1.0/user/can_delete_message", name = "Check if user can delete message with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canDeleteMessage(Authentication authentication, Message message) {
        return ResponseEntity.ok(
                userService.canDeleteMessage(Auth.getUser(authentication), message)
        );
    }

    @GetMapping(value = "/v1.0/user/can_delete_chat", name = "Check if user can delete chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canDeleteChat(Authentication authentication, Group chat) {
        return ResponseEntity.ok(
                userService.canDeleteChat(Auth.getUser(authentication), chat)
        );
    }


    @GetMapping(value = "/check_password")
    public ResponseEntity<Boolean> checkPassword(String userNickname, String verifiablePassword) {
        return ResponseEntity.ok(
                userService.checkPassword(userNickname, verifiablePassword)
        );
    }

    @GetMapping(value = "/check_secret_words")
    public ResponseEntity<Boolean> checkSecretWords(String userNickname,
                                                   int a, String a_value,
                                                   int b, String b_value,
                                                   int c, String c_value) {
        return ResponseEntity.ok(
                userService.checkSecretWords(userNickname,
                        a, a_value,
                        b, b_value,
                        c, c_value)
        );
    }

    @DeleteMapping(value = "/delete")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication) {
        userService.delete(Auth.getUser(authentication));
    }
}
