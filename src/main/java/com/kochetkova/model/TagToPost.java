package com.kochetkova.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tag2post")
public class TagToPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "post_id")
    @NotNull
    private int pastId;

    @Column(name = "tag_id")
    @NotNull
    private int tagId;

    public int getId() {
        return id;
    }

    public int getPastId() {
        return pastId;
    }

    public void setPastId(int pastId) {
        this.pastId = pastId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
