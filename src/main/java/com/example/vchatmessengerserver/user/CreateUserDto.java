package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.files.avatar.AvatarDto;
import lombok.Getter;
import lombok.Setter;

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
    // encoded in base64 format user's avatar
    private AvatarDto avatar;
}
