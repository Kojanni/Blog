package com.kochetkova.service;

import com.kochetkova.model.Tag;

public interface TagService {
    Tag findTag(String tag);
    Tag save(String tagName);
}
