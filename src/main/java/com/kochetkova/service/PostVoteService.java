package com.kochetkova.service;

import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.PostVote;
import com.kochetkova.model.User;

public interface PostVoteService {

    PostVote findByUserAndPost(User user, Post post);

    PostVote save(Post post, byte value, User user);

    PostVote save(PostVote postVote);

    ResultErrorResponse addLike(Post post, User user);

    ResultErrorResponse addDislike(Post post, User user);

}