package com.kochetkova.repository;

import com.kochetkova.model.Post;
import com.kochetkova.model.PostVote;
import com.kochetkova.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {
    PostVote findByUserAndPost(User user, Post post);
}
