package com.kochetkova.service;

import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.api.response.SortedPostsResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.User;

import java.util.List;

public interface PostService {
    List<Post> findAll();
    Post addPost(NewPostRequest newPostRequest, User user);
    Post putPost(int id, NewPostRequest newPostRequest, User user);
    ErrorResponse checkAddedPost(NewPostRequest newPostRequest);
    SortedPostsResponse getSortedPosts(String mode, int offset, int limit);
    Post findById(int id);
    SortedPostsResponse getSortedPostsById(int id, String status, int offset, int limit);
    Post createNewPost(NewPostRequest newPostRequest);
    void getExistPost(NewPostRequest newPostRequest, Post post);
    PostResponse getPostResponseById(int id);
    void upViewCountOfPost(Post post);
    SortedPostsResponse getSortedPostsByQuery(String query, int offset, int limit);
}
