package com.kochetkova.api.request;

import java.time.LocalDateTime;
import java.util.List;


public class AddedPost {
    private LocalDateTime time;
    private int active;
    private String title;
    private List<String> tags;

    private String text;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int isActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
