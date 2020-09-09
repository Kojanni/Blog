package com.kochetkova.repository;

import com.kochetkova.model.Post;
import com.kochetkova.model.TagToPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagToPostRepository extends CrudRepository<TagToPost, Integer> {
    void deleteAllByPost(Post post);


}
