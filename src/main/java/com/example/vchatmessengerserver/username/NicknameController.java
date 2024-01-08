package com.example.vchatmessengerserver.username;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nickname")
public class NicknameController {

    @Autowired
    NicknameService nicknameService;

    @GetMapping(value = "/checkForUser", name = "Check if the user's nickname is correct")
    public @ResponseBody ResponseEntity<Integer> checkForUser(String username) {
        return ResponseEntity.ok(
                nicknameService.checkForUser(username)
        );
    }

    @GetMapping(value = "/checkForChannel", name = "Check if the channel's nickname is correct")
    public @ResponseBody ResponseEntity<Integer> checkForChannel(String username) {
        return ResponseEntity.ok(
                nicknameService.checkForChannel(username)
        );
    }
}
