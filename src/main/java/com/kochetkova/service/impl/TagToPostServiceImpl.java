package com.kochetkova.service.impl;

import com.kochetkova.model.Post;
import com.kochetkova.model.Tag;
import com.kochetkova.model.TagToPost;
import com.kochetkova.repository.TagToPostRepository;
import com.kochetkova.service.TagToPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagToPostServiceImpl implements TagToPostService {
    private TagToPostRepository tagToPostRepository;

    @Autowired
    public TagToPostServiceImpl(TagToPostRepository tagToPostRepository) {
        this.tagToPostRepository = tagToPostRepository;
    }

    @Override
    public TagToPost save(Tag tag, Post post) {
        TagToPost tagToPost = new TagToPost();
        tagToPost.setTag(tag);
        tagToPost.setPost(post);

        return tagToPostRepository.save(tagToPost);
    }

    @Override
    public void deleteByPost(Post post) {
        tagToPostRepository.deleteAllByPost(post);
    }
}
