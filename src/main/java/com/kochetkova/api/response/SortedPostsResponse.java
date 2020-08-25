package com.kochetkova.api.response;

import lombok.Data;

import java.util.List;

@Data
public class SortedPostsResponse {
    private int count;
    private List<PostResponse> posts;
}
