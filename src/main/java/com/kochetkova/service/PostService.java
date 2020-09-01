package com.kochetkova.service;

import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.User;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post addPost(NewPostRequest newPostRequest, User user);
    ErrorResponse checkAddedPost(NewPostRequest newPostRequest);
    boolean checkTitle(String title);
    boolean checkText(String text);
    List<PostResponse> getSortedPosts(String mode);
    Optional<Post> findById(int id);
    Post createNewPost(NewPostRequest newPostRequest);
    void getExistPost(NewPostRequest newPostRequest, Post post);
}
