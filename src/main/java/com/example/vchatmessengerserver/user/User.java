package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.group.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "vchat_user")
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
    private String password;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "vchat_user_group",
            joinColumns = @JoinColumn(name = "vchat_user_id"),
            inverseJoinColumns = @JoinColumn(name = "vchat_group_id"))
    private List<Group> chats = new ArrayList<>();

    @ElementCollection
    @NotNull
    private List<String> secretWords = new ArrayList<>();

    @Schema(example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAEElEQVR4nGJ6VrQAEAAA//8EQgH7dTCZ8gAAAABJRU5ErkJggg==",
            description = "Base64-encoded avatar image thumbnail"   )
    @Column(name = "IMAGE_DATA", columnDefinition = "LONGTEXT", nullable = false, length = 10000000)
    @Lob
    private String imageData;

    @Getter @Setter
    private int typeOfImage;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User &&
                Objects.equals(((User) obj).getId(), this.getId());
    }
}