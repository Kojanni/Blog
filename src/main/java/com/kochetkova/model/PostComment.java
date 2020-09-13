package com.kochetkova.model;

import lombok.Data;
import lombok.Value;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "parent_id")
    private Integer parentId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @NotNull
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT")
    @NotNull
    private String text;
}
