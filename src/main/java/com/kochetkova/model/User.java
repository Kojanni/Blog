package com.kochetkova.model;

import com.kochetkova.api.request.NewUser;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.transaction.Transactional;
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

    @CreationTimestamp
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

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Post> userPosts;

    @OneToMany(mappedBy = "moderator", fetch = FetchType.EAGER)
    private Set<Post> moderationPosts = new HashSet<>();

    @OneToMany(mappedBy = "moderator", fetch = FetchType.EAGER)
    private Set<Post> votes;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<PostComment> comments;

    public User() {
    }

    public User(NewUser newUser) {
        this.name = newUser.getName();
        this.email = newUser.getEmail();
        this.password = newUser.getPassword();
        this.isModerator = 0;
    }
}
