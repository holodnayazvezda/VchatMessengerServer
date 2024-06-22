package com.example.vchatmessengerserver.files.avatar;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "AVATAR")
public class AvatarDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String avatarFileName;
    private Integer avatarType;
    private Integer avatarBackgroundColor;
}
