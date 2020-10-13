package com.kochetkova.service;

import com.kochetkova.api.response.TagResponse;
import com.kochetkova.api.response.TagWeightResponse;
import com.kochetkova.model.Tag;

import java.util.List;

public interface TagService {

    Tag save(String tagName);

    Tag findByTag(String tag);

    List<Tag> findAll();

    TagResponse getTagResponse(Tag tag);

    List<Tag> findAllByNameStartingWith(String query);

    TagWeightResponse getTagWeightResponse(String query);
}
