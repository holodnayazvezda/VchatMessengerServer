package com.example.vchatmessengerserver.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateUserDto {
    private String name;

    private String nickname;

    private String password;

    private List<String> secretWords;

    private String imageData;

    private int typeOfImage;
}
