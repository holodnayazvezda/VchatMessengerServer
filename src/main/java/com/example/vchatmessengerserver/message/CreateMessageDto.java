package com.example.vchatmessengerserver.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageDto {
    private String content;

    private Long messageChatId;
}
