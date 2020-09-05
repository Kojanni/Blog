package com.kochetkova.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kochetkova.converter.LocalDateTimeSerializer;
import com.kochetkova.converter.LocalDatetimeDeserializer;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active")
    @NotNull
    private byte isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status")
    @NotNull
    private ModerationStatus moderationStatus;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDatetimeDeserializer.class)
    private LocalDateTime time;

    @NotNull
    private String title;


    @Column(columnDefinition = "TEXT")
    @NotNull
    private String text;

    @NotNull
    private int viewCount;

    @OneToMany(mappedBy = "post")
    private List<PostVote> votes;

    @OneToMany(mappedBy = "post")
    private List<TagToPost> tags;

    @OneToMany(mappedBy = "post")
    private List<PostComment> comments;
}
