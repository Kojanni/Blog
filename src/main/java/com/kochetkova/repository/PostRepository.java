package com.kochetkova.repository;

import com.kochetkova.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository <Post, Integer> {
    String POST_TIME = "time";

    List<Post> findAll();

    List<Post> findAllById(int id, Pageable pageable);

    List<Post> findAllByOrderByTimeAsc();

    List<Post> findAllByOrderByTimeDesc();
}
