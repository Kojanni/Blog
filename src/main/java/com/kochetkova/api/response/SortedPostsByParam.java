package com.kochetkova.api.response;

import java.util.List;

public class SortedPostsByParam {
    private int count;
    private List<PostByParam> posts;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PostByParam> getPosts() {
        return posts;
    }

    public void setPosts(List<PostByParam> posts) {
        this.posts = posts;
    }


}
