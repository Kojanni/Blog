package com.kochetkova.repository;

import com.kochetkova.model.PostComment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository extends CrudRepository<PostComment, Integer> {

    PostComment findByIdAndPostId(Integer parentId, int postId);
}
