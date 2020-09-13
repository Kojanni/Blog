package com.kochetkova.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private byte value;


}
