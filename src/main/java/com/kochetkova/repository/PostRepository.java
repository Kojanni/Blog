package com.kochetkova.repository;

import com.kochetkova.model.ModerationStatus;
import com.kochetkova.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository <Post, Integer> {
    String POST_TIME = "time";

    Post findById(int id);

    List<Post> findAll();

    List<Post> findAllByIsActiveAndModerationStatus( byte isActive, ModerationStatus moderationStatus, Pageable pageable);

    List<Post> findAllById(int id, Pageable pageable);

    List<Post> findAllById(int id);

    List<Post> findAllByOrderByTimeAsc(Pageable pageable);

    List<Post> findAllByOrderByTimeDesc(Pageable pageable);

    List<Post> findAllByUserIdAndIsActive(int id, byte isActive);

    List<Post> findAllByUserIdAndIsActive(int id, byte isActive, Pageable pageable);

    List<Post> findAllByUserIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus);

    List<Post> findAllByUserIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus, Pageable pageable);

    List<Post> findAllByIsActiveAndModerationStatusAndTextContainingIgnoreCaseOrIsActiveAndModerationStatusAndTitleContainingIgnoreCase(byte isActive, ModerationStatus moderationStatus, String queryText, byte isActive2, ModerationStatus moderationStatus2, String queryTitle, Pageable pageable);

    List<Post> findAllByIsActiveAndModerationStatus(byte isActive, ModerationStatus moderationStatus);

    List<Post> findAllByIsActiveAndModerationStatusAndTextContainingIgnoreCaseOrIsActiveAndModerationStatusAndTitleContainingIgnoreCase(byte isActive, ModerationStatus moderationStatus, String query, byte isActive2, ModerationStatus moderationStatus2, String query2);

    List<Post> findAllByTimeBetween(LocalDateTime timeStart, LocalDateTime timeEnd);

    List<Post> findAllByTimeBetween(LocalDateTime timeStart, LocalDateTime timeEnd, Pageable pageable);

    List<Post> findAllByModeratorIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus);

    List<Post> findAllByModeratorIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus, Pageable pageable);
}
