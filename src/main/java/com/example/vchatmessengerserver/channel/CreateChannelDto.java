package com.example.vchatmessengerserver.channel;

import com.example.vchatmessengerserver.group.CreateGroupDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChannelDto extends CreateGroupDto {
    private String nickname;
}
