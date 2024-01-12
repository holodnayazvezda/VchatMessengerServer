package com.example.vchatmessengerserver.channel;

import com.example.vchatmessengerserver.group.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;

@Entity(name = "vchat_channel")
@Getter
@Setter
public class Channel extends Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true, length = 30)
    private String nickname;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Channel &&
                Objects.equals(((Channel) obj).getId(), this.getId());
    }
}
