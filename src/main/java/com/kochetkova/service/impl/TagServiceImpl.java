package com.kochetkova.service.impl;

import com.kochetkova.api.response.TagResponse;
import com.kochetkova.api.response.TagWeightResponse;
import com.kochetkova.model.Tag;
import com.kochetkova.repository.PostRepository;
import com.kochetkova.repository.TagRepository;
import com.kochetkova.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {
    private TagRepository tagRepository;
    private PostRepository postRepository;
    private DecimalFormat df = new DecimalFormat("#.##");

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Tag save(String tagName) {
        Tag tag = new Tag();
        tag.setName(tagName);
        tag = tagRepository.save(tag);
        return tag;
    }

    @Override
    public Tag findByTag(String tag) {
        return tagRepository.findByName(tag);
    }

    /**
     * получить список всех существующих тегов в БД
     *
     * @return List<Tag>
     */
    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    /**
     * преобразование тега в тегОтвет(имя тега + вес)
     *
     * @param tag - тег из БД
     * @return TagResponse
     */
    @Override
    public TagResponse getTagResponse(Tag tag) {
        TagResponse tagResponse = new TagResponse();
        tagResponse.setName(tag.getName());
        tagResponse.setWeight(getWeightTag(tag));
        return tagResponse;
    }

    /**
     * Определение веса тега
     *
     * @param tag - тег
     * @return вес тега double
     */
    private double getWeightTag(Tag tag) {
        return Double.parseDouble(df.format((double) tag.getPosts().size() / postRepository.findAll().size()).replace(",", "."));
    }

    /**
     * Поиск всех тегов начинающихся на заданную последовательность
     *
     * @param query - заданная последовательность
     * @return лист тэгов
     */
    @Override
    public List<Tag> findAllByNameStartingWith(String query) {
        return tagRepository.findAllByNameStartingWith(query);
    }

    /**
     * Получение списка тегов с весом, где имена тегов начинаются на заданную последовательность
     *
     * @param query - заданная последовательность
     * @return TagWeightResponse - представляет список тег-вес
     */
    @Override
    public TagWeightResponse getTagWeightResponse(String query) {
        TagWeightResponse tagWeightResponse = new TagWeightResponse();

        List<Tag> tags;
        if (query == null) {
            tags = tagRepository.findAll();
        } else {
            tags = findAllByNameStartingWith(query);
        }
        List<TagResponse> tagResponses = tags.stream().map(this::getTagResponse).collect(Collectors.toList());
        tagWeightResponse.setTags(tagResponses);

        return tagWeightResponse;
    }
}
