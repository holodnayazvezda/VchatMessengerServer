package com.example.vchatmessengerserver.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
// dto class for user creation
public class CreateUserDto {
    // user's name field
    private String name;
    // user's nickname field
    private String nickname;
    // user's password field
    private String password;
    // list of user's secret words
    private List<String> secretWords;
    // encoded in base64 format user's image (avatar)
    private String imageData;
    // type of user's image
    private int typeOfImage;
}
