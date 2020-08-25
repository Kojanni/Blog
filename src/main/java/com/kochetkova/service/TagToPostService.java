package com.kochetkova.service;

import com.kochetkova.model.Post;
import com.kochetkova.model.Tag;
import com.kochetkova.model.TagToPost;

public interface TagToPostService {
    TagToPost save(Tag tag, Post post);
}
