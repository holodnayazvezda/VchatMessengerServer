package com.example.vchatmessengerserver.group;

import com.example.vchatmessengerserver.files.avatar.AvatarDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateGroupDto {
    private String name;

    private Long unreadMessagesCount;

    private List<Long> membersIds = new ArrayList<>();

    private AvatarDTO avatarDTO;
}
