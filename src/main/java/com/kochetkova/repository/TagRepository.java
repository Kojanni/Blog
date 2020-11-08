package com.kochetkova.repository;

import com.kochetkova.model.ModerationStatus;
import com.kochetkova.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
    Tag findByName(String name);

    List<Tag> findAll();

    List<Tag> findAllByNameStartingWith(String query);
}
