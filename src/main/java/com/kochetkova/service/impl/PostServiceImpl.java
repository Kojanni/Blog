package com.kochetkova.service.impl;

import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.model.ModerationStatus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private TagService tagService;
    private TagToPostService tagToPostService;
    private final String[] modeSort = {"recent", "popular", "best", "early"};
    private final int modeRecent = 0;
    private final int modePopular = 1;
    private final int modeBest = 2;
    private final int modeEarly = 3;

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

//Сохранить пост в БД
    private Post savePost(Post post) {
        return postRepository.save(post);
    }

//Добавить пост
    @Override
    public Post addPost(NewPostRequest newPostRequest, User user) {
        Post post = createNewPost(newPostRequest);
        post.setUser(user);
        post = savePost(post);

        Set<String> tagsName = newPostRequest.getTags();

        Post finalPost = post;
        tagsName.forEach(name ->{
            Tag tag = tagService.findTag(name);
            if (tag == null) {
                tag = tagService.save(name);
            }
            tagToPostService.save(tag, finalPost);
        });
        return post;
    }

//Проверка данных добавляемого поста
    @Override
    public ErrorResponse checkAddedPost(NewPostRequest newPostRequest) {
        ErrorResponse.ErrorResponseBuilder errorBuilder = ErrorResponse.builder();
        if (!checkText(newPostRequest.getText())) {
            errorBuilder.text("Текс публикации слишком короткий");
        }
        if (!checkTitle(newPostRequest.getTitle())) {
            errorBuilder.title("Заголовок не установлен или короткий");
        }

        return errorBuilder.build();
    }
//проверка длины заголовка
    @Override
    public boolean checkTitle(String title) {
        return title.length() >= minLengthTitle;
    }

//проверка длины текста
    @Override
    public boolean checkText(String text) {
        return getAnnounce(text).length() >= minLengthText;
    }

    //текст поста без тегов и форматирования
    private String getAnnounce(String text) {
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
        return textCleared.toString();
    }
    //Получение списка постов в соотвествии с режимом(параметр mode)
    private List<Post> getModePosts(String mode) {
        List<Post> posts = new ArrayList<>();
        if (mode.equalsIgnoreCase(modeSort[modeRecent])){ //по дате публикации новые
            posts = postRepository.findAllByOrderByTimeDesc();
        } else if (mode.equalsIgnoreCase(modeSort[modeBest])){ //по убыванию лайков

        } else if (mode.equalsIgnoreCase(modeSort[modePopular])){ //по убыванию комментов

        } else if (mode.equalsIgnoreCase(modeSort[modeEarly])){ //по дате публикации старые

        }
        return posts;
    }

    //postResponse на основе post
    private PostResponse createPostResponse(Post post) {
        PostResponse.PostResponseBuilder postBuilder= PostResponse.builder();
        postBuilder.id(post.getId());
        postBuilder.time(post.getTime());
        postBuilder.title(post.getTitle());
        postBuilder.announce(getAnnounce(post.getText()));
        postBuilder.viewCount(post.getViewCount());



        return postBuilder.build();
    }

//Получение списка PostResponse в соотвествии с режимом(параметр mode)
    @Override
    public List<PostResponse> getSortedPosts(String mode) {
        List<Post> posts = getModePosts(mode);
        List<PostResponse> postResponses = new ArrayList<>();
        posts.forEach(post -> postResponses.add(createPostResponse(post)));
        return postResponses;
    }

    //поиск поста по id
    @Override
    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    //получение нового объекта Post из данных поступивших по запросу в формате AddedPost
    @Override
    public Post createNewPost(NewPostRequest newPostRequest) {
        //todo
        //сделать проверку времени
        Post post = new Post();
        post.setTime(newPostRequest.getTimestamp());
        post.setIsActive(newPostRequest.getActive());
        post.setTitle(newPostRequest.getTitle());
        post.setText(newPostRequest.getText());
        post.setViewCount(0);
        post.setModerationStatus(ModerationStatus.NEW);
        return post;
    }

    //инициализация полей объекта Post из данных поступивших по запросу в формате AddedPost
    @Override
    public void getExistPost(NewPostRequest newPostRequest, Post post) {
        //todo
        //сделать проверку времени
        post.setTime(newPostRequest.getTimestamp());
        post.setIsActive(newPostRequest.getActive());
        post.setTitle(newPostRequest.getTitle());
        post.setText(newPostRequest.getText());
        post.setViewCount(0);
        post.setModerationStatus(ModerationStatus.NEW);
    }
}
