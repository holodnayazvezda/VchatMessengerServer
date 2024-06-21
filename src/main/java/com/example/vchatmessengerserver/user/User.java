package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.group.Group;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "VCHAT_USER")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(length = 30)
    private String name;

    @NonNull
    @Column(unique = true, length = 30)
    private String nickname;

    @NonNull
    @Column(length = 200)
    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "VCHAT_USER_CHATS",
            joinColumns = @JoinColumn(name = "VCHAT_USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "VCHAT_GROUP_ID"))
    @JsonIgnore
    private List<Group> chats = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @NotNull
    @JsonIgnore
    private List<String> secretKey = new ArrayList<>();

    @Schema(example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAEElEQVR4nGJ6VrQAEAAA//8EQgH7dTCZ8gAAAABJRU5ErkJggg==",
            description = "Base64-encoded avatar image thumbnail"   )
    @Column(name = "AVATAR_DATA", columnDefinition = "LONGTEXT", nullable = false, length = 100000000)
    @Lob
    private String avatarData;

    @Getter @Setter
    private int avatarType;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User &&
                Objects.equals(((User) obj).getId(), this.getId());
    }
}