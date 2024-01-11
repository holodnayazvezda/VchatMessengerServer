package com.example.vchatmessengerserver.group;

import com.example.vchatmessengerserver.message.Message;
import com.example.vchatmessengerserver.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "vchat_group")
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(length = 30)
    private String name;

    private Long unreadMsgCount;

    private Integer type;

    private Integer typeOfImage;

    @NonNull
    private Long ownerId;

    @NonNull
    private ZonedDateTime creationDate;

    @OneToMany
    private List<Message> messages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "chats")
    @JsonIgnore
    private List<User> members = new ArrayList<>();
    
    @Schema(example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAEElEQVR4nGJ6VrQAEAAA//8EQgH7dTCZ8gAAAABJRU5ErkJggg==",
            description = "Base64-encoded avatar image thumbnail", required = true)
    @Column(name = "IMAGE_DATA", columnDefinition = "LONGTEXT", nullable = false, length = 10000000)
    private String imageData;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Group &&
                Objects.equals(((Group) obj).getId(), this.getId());
    }
}
