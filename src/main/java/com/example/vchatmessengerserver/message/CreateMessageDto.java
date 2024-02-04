package com.example.vchatmessengerserver.message;

import com.example.vchatmessengerserver.group.Group;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.server.GracefulShutdownCallback;

@Getter
@Setter
public class CreateMessageDto {
    private String content;

    private Group messageChat;
}
