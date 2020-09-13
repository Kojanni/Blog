package com.kochetkova.service;

import com.kochetkova.api.request.NewCommentRequest;
import com.kochetkova.api.response.AddedCommentIdResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.model.PostComment;
import com.kochetkova.model.User;

public interface PostCommentService {
    PostComment save(PostComment postComment);
    ResultErrorResponse checkNewCommentRequestData(NewCommentRequest newCommentRequest);
    AddedCommentIdResponse addNewComment(NewCommentRequest newCommentRequest, User user);
}
