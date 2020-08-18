package com.kochetkova.api.response;

import lombok.Data;

import java.util.List;

@Data
public class SortedPosts {
    private int count;
    private List<PostResponse> posts;
}
