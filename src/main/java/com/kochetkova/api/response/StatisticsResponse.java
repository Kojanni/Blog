package com.kochetkova.api.response;

import lombok.Data;

@Data
public class StatisticsResponse {
    private int postsCount;
    private int LikesCount;
    private int dislikeCount;
    private int viewsCount;
    private String firstPublication;
}
