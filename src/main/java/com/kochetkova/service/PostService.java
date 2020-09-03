package com.kochetkova.service;

import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.response.CommentResponse;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.api.response.SortedPostsResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.PostComment;
import com.kochetkova.model.User;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post addPost(NewPostRequest newPostRequest, User user);
    ErrorResponse checkAddedPost(NewPostRequest newPostRequest);
    boolean checkTitle(String title);
    boolean checkText(String text);
    SortedPostsResponse getSortedPosts(String mode, int offset, int limit);
    Post findById(int id);
    List<Post> findAllById(int id, int offset, int itemPerPage);
    Post createNewPost(NewPostRequest newPostRequest);
    void getExistPost(NewPostRequest newPostRequest, Post post);
    CommentResponse createCommentResponse(PostComment postComment);
}
