package com.example.vchatmessengerserver.user;


import com.example.vchatmessengerserver.auth.Auth;
import com.example.vchatmessengerserver.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;

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
}
