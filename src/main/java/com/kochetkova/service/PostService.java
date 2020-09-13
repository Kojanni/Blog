package com.kochetkova.service;

import com.kochetkova.api.request.ModerationPostRequest;
import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.response.*;
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

    CalendarResponse getPostsCountByYear(Integer year);

    SortedPostsResponse getSortedPostsByQuery(String query, int offset, int limit);

    SortedPostsResponse getSortedPostsByDate(String date, int offset, int limit);

    SortedPostsResponse getSortedPostsByTag(String tag, int offset, int limit);

    SortedPostsResponse getSortedPostsForModeration(User user, String status, int offset, int limit);

    boolean changeModerationStatus(ModerationPostRequest moderationPostRequest);

    StatisticsResponse getUserStatistics(User user);

    StatisticsResponse getStatistics();
}
