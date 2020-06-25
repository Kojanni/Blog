package com.kochetkova.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tag2post")
public class TagToPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    @NotNull
    private Tag tag;

}
