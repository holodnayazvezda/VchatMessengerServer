package com.example.vchatmessengerserver.message;


import com.example.vchatmessengerserver.group.Group;
import com.example.vchatmessengerserver.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.lang.NonNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "VCHAT_TEXT_MESSAGE")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(length = 20000)
    private String content;

    @NonNull
    @ManyToOne
    @JsonIgnore
    private Group messageChat;

    @NonNull
    private ZonedDateTime creationDate;

    @ManyToOne
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> readers = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Message &&
                Objects.equals(((Message) obj).getId(), this.getId());
    }
}
