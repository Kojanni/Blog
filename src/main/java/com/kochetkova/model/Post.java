package com.kochetkova.model;

import com.kochetkova.api.request.AddedPost;
import lombok.Data;
import lombok.Value;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    private LocalDateTime time;

    @NotNull
    private String title;

    @NotNull
    private String text;

    @NotNull
    private int viewCount;

    @OneToMany(mappedBy = "post")
    private Set<PostVote> votes;

    @OneToMany(mappedBy = "post")
    private Set<TagToPost> tags;

    @OneToMany(mappedBy = "post")
    private Set<PostComment> comments;

    public void setPostData(AddedPost addedPost) {
        //todo
        //сделать проверку времени
        this.time = addedPost.getTime();
        this.isActive = addedPost.getActive();
        this.title = addedPost.getTitle();
        this.text = addedPost.getText();
        this.viewCount = 0;
        moderationStatus = ModerationStatus.NEW;
    }
}
