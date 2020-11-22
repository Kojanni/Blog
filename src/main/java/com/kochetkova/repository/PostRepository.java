package com.kochetkova.repository;

import com.kochetkova.model.User;
import com.kochetkova.service.impl.enums.ModerationStatus;
import com.kochetkova.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    String POST_TIME = "time";

    int countByIsActiveAndModerationStatusAndTimeBefore(byte isActive, ModerationStatus moderationStatus, LocalDateTime time);

    int countByTimeBetweenAndIsActiveAndModerationStatusAndTimeBefore(LocalDateTime timeStart, LocalDateTime timeEnd, byte isActive, ModerationStatus moderationStatus, LocalDateTime timeNow);


    List<Post> findAllByIsActiveAndModerationStatusAndTimeBefore(byte isActive, ModerationStatus moderationStatus, LocalDateTime time);

    List<Post> findAllByIsActiveAndModerationStatusAndTimeBefore(byte isActive, ModerationStatus moderationStatus, LocalDateTime time, Pageable pageable);

    int countAllByIsActiveAndModerationStatusAndTimeBefore(byte isActive, ModerationStatus moderationStatus, LocalDateTime time);

    @Query("SELECT p "
            + "FROM Post p "
            + "LEFT JOIN p.comments c "
            + "WHERE p.isActive = :isActive "
            + "AND p.moderationStatus = :moderationStatus "
            + "AND p.time <= :timeNow "
            + "GROUP BY p.id "
            + "ORDER BY COUNT(c.id) DESC ")
    List<Post> findAllOrderByComments(@Param("isActive") byte isActive,
                                      @Param("moderationStatus") ModerationStatus moderationStatus,
                                      @Param("timeNow") LocalDateTime timeNow,
                                      Pageable pageable);

    @Query("SELECT p "
            + "FROM Post p "
            + "LEFT JOIN p.votes v "
            + "ON v.value = 1"
            + "WHERE p.isActive = :isActive "
            + "AND p.moderationStatus = :moderationStatus "
            + "AND p.time <= :timeNow "
            + "GROUP BY p.id "
            + "ORDER BY COUNT(v.id) DESC ")
    List<Post> findAllOrderByLikes(@Param("isActive") byte isActive,
                                   @Param("moderationStatus") ModerationStatus moderationStatus,
                                   @Param("timeNow") LocalDateTime timeNow,
                                   Pageable pageable);


    List<Post> findAllById(int id, Pageable pageable);

    List<Post> findAllById(int id);

    List<Post> findAllByIsActiveAndModerationStatusAndTimeBeforeOrderByTimeAsc(byte isActive, ModerationStatus moderationStatus, LocalDateTime timeNow, Pageable pageable);

    List<Post> findAllByIsActiveAndModerationStatusAndTimeBeforeOrderByTimeDesc(byte isActive, ModerationStatus moderationStatus, LocalDateTime timeNow, Pageable pageable);

    List<Post> findAllByUserIdAndIsActive(int id, byte isActive);

    List<Post> findAllByUserIdAndIsActive(int id, byte isActive, Pageable pageable);

    List<Post> findAllByUserIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus);

    List<Post> findAllByUserIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus, Pageable pageable);

    List<Post> findAllByTextContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBeforeOrTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(String queryText, byte isActive, ModerationStatus moderationStatus, LocalDateTime timeNow, String queryTitle, byte isActive2, ModerationStatus moderationStatus2, LocalDateTime timeNow2);

    List<Post> findAllByTextContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBeforeOrTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(String queryText, byte isActive, ModerationStatus moderationStatus, LocalDateTime timeNow, String queryTitle, byte isActive2, ModerationStatus moderationStatus2, LocalDateTime timeNow2, Pageable pageable);

    int countByTextContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBeforeOrTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(String queryText, byte isActive, ModerationStatus moderationStatus, LocalDateTime timeNow, String queryTitle, byte isActive2, ModerationStatus moderationStatus2, LocalDateTime timeNow2);


    List<Post> findAllByTimeBetweenAndIsActiveAndModerationStatusAndTimeBefore(LocalDateTime timeStart, LocalDateTime timeEnd, byte isActive, ModerationStatus moderationStatus, LocalDateTime timeNow);

    List<Post> findAllByTimeBetweenAndIsActiveAndModerationStatusAndTimeBefore(LocalDateTime timeStart, LocalDateTime timeEnd, byte isActive, ModerationStatus moderationStatus, LocalDateTime timeNow, Pageable pageable);

    List<Post> findAllByModeratorIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus);

    List<Post> findAllByModeratorIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus, Pageable pageable);

    int countByModeratorIdAndIsActiveAndModerationStatus(int id, byte isActive, ModerationStatus moderationStatus);

    @Modifying
    @Query("UPDATE Post p "
            + "SET p.viewCount  = p.viewCount + 1 "
            + "WHERE p.id = :postID "
            + "AND ((:user is not null AND p.user <> :user) "
            + "OR (:user is null)) ")
    int incrementPostViewCountIfNotUser(@Param("postID") int postID, @Param("user") User user);
}
