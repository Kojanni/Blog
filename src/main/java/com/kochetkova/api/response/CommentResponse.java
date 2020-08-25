package com.kochetkova.api.response;

import lombok.Data;

@Data
public class CommentResponse {
    private int id;
    private String time;
    private String text;
    private UserResponse userResponse;
}
