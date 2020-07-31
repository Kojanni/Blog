package com.kochetkova.api.response;

import lombok.Data;

@Data
public class Comment {
    private int id;
    private String time;
    private String text;
    private User user;
}
