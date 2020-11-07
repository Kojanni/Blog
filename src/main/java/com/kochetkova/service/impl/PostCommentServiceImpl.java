package com.kochetkova.service.impl;

import com.kochetkova.api.request.NewCommentRequest;
import com.kochetkova.api.response.AddedCommentIdResponse;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.model.PostComment;
import com.kochetkova.model.User;
import com.kochetkova.repository.PostCommentRepository;
import com.kochetkova.repository.PostRepository;
import com.kochetkova.service.PostCommentService;
import com.kochetkova.service.PostService;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostCommentServiceImpl implements PostCommentService {
    private PostCommentRepository postCommentRepository;
    private PostService postService;

    @Value("${blog.post.comment.text.length.min}")
    private int minLengthText;

    @Autowired
    public PostCommentServiceImpl(PostCommentRepository postCommentRepository, PostService postService) {
        this.postCommentRepository = postCommentRepository;
        this.postService = postService;
    }


    /**
     * Сохранить postComment объект в БД
     *
     * @param postComment - saving comment for post
     * @return PostComment
     */
    @Override
    public PostComment save(PostComment postComment) {
        return postCommentRepository.save(postComment);
    }

    /**
     * Проверить на корректность данные для добавляемого комментария
     *
     * @param newCommentRequest - данные нового комментария
     * @return ResultErrorResponse
     */
    @Override
    public ResultErrorResponse checkNewCommentRequestData(NewCommentRequest newCommentRequest) {
        ErrorResponse.ErrorResponseBuilder errorBuilder = ErrorResponse.builder();
        ResultErrorResponse resultError = new ResultErrorResponse();

        if (!checkText(newCommentRequest.getText())) {
            errorBuilder.text("Текст комментария не задан или слишком короткий.\nМинимальная длина: " + minLengthText + " символов");
        }
//todo
        if ( newCommentRequest.getParentId() != null && postCommentRepository.findByIdAndPostId(newCommentRequest.getParentId(), newCommentRequest.getPostId()) == null) {
            errorBuilder.badRequest(true);
        }


        ErrorResponse error = errorBuilder.build();
        if (!error.isPresent()) {
            resultError.setResult(true);
        } else {
            resultError.setErrors(error);
        }
        return resultError;
    }

    /**
     * проверка длины текста
     *
     * @param text - текст
     */
    private boolean checkText(String text) {
        return html2text(text).length() >= minLengthText;
    }

    /**
     * текст поста без тегов и форматирования
     *
     * @param html - текст в формате html
     * @return String - строка без форматирования
     */
    private String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    /**
     * Добавление нового комментария
     *
     * @param newCommentRequest - данные для добавления
     * @return AddedCommentIdResponse - номер коммента
     */
    @Override
    public AddedCommentIdResponse addNewComment(NewCommentRequest newCommentRequest, User user) {
        PostComment postComment = new PostComment();


        postComment.setPost(postService.findById(newCommentRequest.getPostId()));
        if (newCommentRequest.getParentId() != null) {
            postComment.setParentId(newCommentRequest.getParentId());
        }
        postComment.setText(newCommentRequest.getText());
        postComment.setTime(LocalDateTime.now());
        postComment.setUser(user);

        AddedCommentIdResponse addedCommentIdResponse = new AddedCommentIdResponse();
        addedCommentIdResponse.setId(postCommentRepository.save(postComment).getId());

        return addedCommentIdResponse;
    }
}
