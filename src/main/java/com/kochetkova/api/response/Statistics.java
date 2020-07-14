package com.kochetkova.api.response;

public class Statistics {
    private int postsCount;
    private int LikesCount;
    private int dislikeCount;
    private int viewsCount;
    private String firstPublication;

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getLikesCount() {
        return LikesCount;
    }

    public void setLikesCount(int likesCount) {
        LikesCount = likesCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getFirstPublication() {
        return firstPublication;
    }

    public void setFirstPublication(String firstPublication) {
        this.firstPublication = firstPublication;
    }
}
