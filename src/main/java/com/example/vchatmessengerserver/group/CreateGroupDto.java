package com.example.vchatmessengerserver.group;

import com.example.vchatmessengerserver.message.Message;
import com.example.vchatmessengerserver.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateGroupDto {
    private String name;

    private Long unreadMessagesCount;

    private Integer typeOfImage;

    private List<Long> membersIds = new ArrayList<>();

    private String imageData;
}
