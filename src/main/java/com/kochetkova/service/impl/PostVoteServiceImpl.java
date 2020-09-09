package com.kochetkova.service.impl;

import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.PostVote;
import com.kochetkova.model.User;
import com.kochetkova.repository.PostVoteRepository;
import com.kochetkova.service.PostVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostVoteServiceImpl implements PostVoteService {

    private PostVoteRepository postVoteRepository;

    @Autowired
    public PostVoteServiceImpl(PostVoteRepository postVoteRepository) {
        this.postVoteRepository = postVoteRepository;
    }

    /**
     * Найти голос пользователя к посту
     *
     * @param user - пользователь, поставивший оценку
     * @param post - пост, который оценили
     * @return PostVote или null
     */
    @Override
    public PostVote findByUserAndPost(User user, Post post) {
        return postVoteRepository.findByUserAndPost(user, post);
    }

    /**
     * Добавление нового голоса от пользователя к посту
     *
     * @param post  - пост
     * @param value - значение:
     *              Like = 1,
     *              dislike = -1;
     * @param user  - пользователь
     * @return Добавленный пост в БД
     */
    @Override
    public PostVote save(Post post, byte value, User user) {
        PostVote postVote = new PostVote();
        postVote.setPost(post);
        postVote.setUser(user);
        postVote.setValue(value);
        postVote.setTime(LocalDateTime.now());
        return postVoteRepository.save(postVote);
    }

    /**
     * сохранение PostVote в БД
     *
     * @param postVote - измененные данные для оценки поста от пользователя
     * @return PostVote
     */
    @Override
    public PostVote save(PostVote postVote) {
        return postVoteRepository.save(postVote);
    }

    /**
     * Добавление лайка к посту
     *
     * @param post - пост
     * @param user - пользователь, который оценил
     * @return ResultErrorResponse - result:
     * true - добавлен,
     * false - не добавлен
     */
    @Override
    public ResultErrorResponse addLike(Post post, User user) {
        byte valueVote = 1;
        return addValueVote(post, user, valueVote);
    }

    /**
     * Добавление дизлайка к посту
     *
     * @param post - пост
     * @param user - пользователь, который оценил
     * @return ResultErrorResponse - result:
     * true - добавлен,
     * false - не добавлен
     */
    @Override
    public ResultErrorResponse addDislike(Post post, User user) {
        byte valueVote = -1;
        return addValueVote(post, user, valueVote);
    }

    /**
     * добавление лайка или дизлайк к посту пользователем
     *
     * @param post      - пост
     * @param user      - пользователь
     * @param valueVote - лайка = 1, дизлайк = -1
     * @return ResultErrorResponse - result:
     * true - добавлен,
     * false - не добавлен
     */
    private ResultErrorResponse addValueVote(Post post, User user, byte valueVote) {
        ResultErrorResponse resultErrorResponse = new ResultErrorResponse();

        PostVote postVote = findByUserAndPost(user, post);
        if (postVote == null) {
            postVote = save(post, valueVote, user);
            resultErrorResponse.setResult(postVote != null);
        } else {
            if (postVote.getValue() == valueVote) {
                resultErrorResponse.setResult(false);
            } else {
                postVote.setValue(valueVote);
                save(postVote);
                resultErrorResponse.setResult(true);
            }
        }
        return resultErrorResponse;
    }
}
