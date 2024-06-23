package com.example.vchatmessengerserver.files.avatar;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "AVATAR")
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String avatarFileName;
    @NonNull
    private Integer avatarType;
    @NonNull
    private String avatarBackgroundColor;
}
