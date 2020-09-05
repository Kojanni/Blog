package com.kochetkova.repository;

import com.kochetkova.model.ModerationStatus;
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

    List<Post> findAllById(int id);

    List<Post> findAllByOrderByTimeAsc(Pageable pageable);

    List<Post> findAllByOrderByTimeDesc(Pageable pageable);

    List<Post> findAllByUserIdAndIsActive(int id, byte isActive);

    List<Post> findAllByUserIdAndIsActive(int id, byte isActive, Pageable pageable);

    List<Post> findAllByUserIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus);

    List<Post> findAllByUserIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus, Pageable pageable);

}
