package com.kochetkova.service.impl;

import com.kochetkova.model.Tag;
import com.kochetkova.repository.TagRepository;
import com.kochetkova.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {
    private TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag save(String tagName) {
        Tag tag = new Tag();
        tag.setName(tagName);
        tag = tagRepository.save(tag);
        return tag;
    }

    @Override
    public Tag findTag(String tag) {
        return tagRepository.findByName(tag);
    }
}
