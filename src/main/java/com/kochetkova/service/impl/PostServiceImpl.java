package com.kochetkova.service.impl;

import com.kochetkova.api.request.AddedPost;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.Tag;
import com.kochetkova.model.User;
import com.kochetkova.repository.PostRepository;
import com.kochetkova.service.PostService;
import com.kochetkova.service.TagService;
import com.kochetkova.service.TagToPostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private TagService tagService;
    private TagToPostService tagToPostService;

    @Value("${blog.post.title.length.min}")
    private int minLengthTitle;

    @Value("${blog.post.text.length.min}")
    private int minLengthText;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagService tagService, TagToPostService tagToPostService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.tagToPostService = tagToPostService;
    }

    private Post savePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post addPost(AddedPost addedPost, User user) {
        Post post = new Post();
        post.setPostData(addedPost);
        post.setUser(user);
        post = savePost(post);

        Set<String> tagsName = addedPost.getTags();
        Post finalPost = post;
        tagsName.forEach(name ->{
            Tag tag = tagService.findTag(name);
            if (tag == null) {
                tag = tagService.save(name);
            }
            tagToPostService.save(tag, finalPost);
        });




        System.out.println(">>>ADD POST " + post.getId());
        return post;
    }

    @Override
    public ErrorResponse checkAddedPost(AddedPost addedPost) {
        ErrorResponse.ErrorResponseBuilder errorBuilder = ErrorResponse.builder();
        if (!checkText(addedPost.getText())) {
            errorBuilder.text("Текс публикации слишком короткий");
        }
        if (!checkTitle(addedPost.getTitle())) {
            errorBuilder.title("Заголовок не установлен или короткий");
        }

        return errorBuilder.build();
    }

    @Override
    public boolean checkTitle(String title) {
        return title.length() >= minLengthTitle;
    }

    @Override
    public boolean checkText(String text) {
        StringBuilder textCleared = new StringBuilder();
        if (text.contains("span")) {
            Document doc = Jsoup.parse(text);
            Elements paragraphs = doc.select("span");
            for (Element paragraph : paragraphs) {
                textCleared.append(paragraph.text());
            }
        } else {
            textCleared.append(text);
        }
        return textCleared.toString().length() >= minLengthText;
    }
}
