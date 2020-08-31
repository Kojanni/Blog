package com.kochetkova.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> userPosts;

    @OneToMany(mappedBy = "moderator", fetch = FetchType.LAZY)
    private List<Post> moderationPosts;

    @OneToMany(mappedBy = "moderator", fetch = FetchType.LAZY)
    private List<Post> votes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<PostComment> comments;

}
