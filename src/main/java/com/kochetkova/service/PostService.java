package com.kochetkova.service;

import com.kochetkova.api.request.AddedPost;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.model.ModerationStatus;
import com.kochetkova.model.Post;
import com.kochetkova.model.User;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post addPost(AddedPost addedPost, User user);
    ErrorResponse checkAddedPost(AddedPost addedPost);
    boolean checkTitle(String title);
    boolean checkText(String text);
    List<PostResponse> getSortedPosts(String mode);
    Optional<Post> findById(int id);
    Post createNewPost(AddedPost addedPost);
    void getExistPost(AddedPost addedPost, Post post);
}
