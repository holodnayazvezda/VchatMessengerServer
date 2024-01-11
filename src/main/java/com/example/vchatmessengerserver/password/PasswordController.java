package com.example.vchatmessengerserver.password;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
public class PasswordController {

    @GetMapping(value = "/checkCorrectness")
    public @ResponseBody ResponseEntity<Integer> checkCorrectness(String password) {
        return ResponseEntity.ok(
                PasswordService.checkCorrectness(password)
        );
    }

    @GetMapping(value = "/checkConfirmation")
    public @ResponseBody ResponseEntity<Boolean> checkConfirmation(String password1, String password2) {
        return ResponseEntity.ok(
                PasswordService.checkConfirmation(password1, password2)
        );
    }

    @GetMapping(value = "/checkEverything")
    public @ResponseBody ResponseEntity<Integer> checkEverything(String password1, String password2) {
        return ResponseEntity.ok(
                PasswordService.checkEverything(password1, password2)
        );
    }
}
