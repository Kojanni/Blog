package com.kochetkova.api.response;

import com.kochetkova.model.Post;

import java.util.List;

public class SortedPosts {
    private int count;
    private List<Post> posts;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void addPost(Post post) {
        posts.add(post);
    }
}
