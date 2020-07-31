package com.kochetkova.api.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddedPost {
    private LocalDateTime time;
    private int active;
    private String title;
    private List<String> tags;
    private String text;
}
