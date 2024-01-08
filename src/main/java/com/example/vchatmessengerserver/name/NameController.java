package com.example.vchatmessengerserver.name;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping(value = "/check", name = "Check if the user's (channel's or group's) name is correct")
    public @ResponseBody ResponseEntity<Integer> checkName(String name) {
        return ResponseEntity.ok(
                NameService.checkName(name)
        );
    }
}
