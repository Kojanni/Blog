package com.kochetkova.model;

import com.kochetkova.api.request.NewUser;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_moderator")
    @NotNull
    private Byte isModerator;

    @Column(name = "reg_time")
    @NotNull
    private LocalDateTime regTime;

    @Column(nullable = false)
    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private String code;

    private String photo;

    @OneToMany(mappedBy = "user")
    private Set<Post> userPosts;

    @OneToMany(mappedBy = "moderator")
    private Set<Post> moderationPosts;

    @OneToMany(mappedBy = "moderator")
    private Set<Post> votes;

    @OneToMany(mappedBy = "user")
    private Set<PostComment> comments;

    public User() {
    }

    public User(NewUser newUser) {
        this.name = newUser.getName();
        this.email = newUser.getEmail();
        this.password = newUser.getPassword();
        this.regTime = LocalDateTime.now();
        this.isModerator = 0;

    }
}
