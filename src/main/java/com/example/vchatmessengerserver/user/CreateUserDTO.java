package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.files.avatar.AvatarDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// dto class for user creation
public class CreateUserDTO {
    // user's name field
    private String name;
    // user's nickname field
    private String nickname;
    // user's password field
    private String password;
    // user's avatarDTO DTO
    private AvatarDTO avatarDTO;
}
