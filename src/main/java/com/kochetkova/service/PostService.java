package com.kochetkova.service;

import com.kochetkova.api.request.AddedPost;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.User;

public interface PostService {
    Post addPost(AddedPost addedPost, User user);
    ErrorResponse checkAddedPost(AddedPost addedPost);
    boolean checkTitle(String title);
    boolean checkText(String text);

}
