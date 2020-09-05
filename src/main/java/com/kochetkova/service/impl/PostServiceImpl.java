package com.kochetkova.service.impl;

import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.response.CommentResponse;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.api.response.SortedPostsResponse;
import com.kochetkova.model.*;
import com.kochetkova.repository.PostRepository;
import com.kochetkova.service.PostService;
import com.kochetkova.service.TagService;
import com.kochetkova.service.TagToPostService;
import com.kochetkova.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private TagService tagService;
    private UserService userService;
    private TagToPostService tagToPostService;
    private final String[] MODE = {"recent", "popular", "best", "early"};
    private final int RECENT = 0;
    private final int POPULAR = 1;
    private final int BEST = 2;
    private final int EARLY = 3;
    private final String[] STATUS = {"inactive", "pending", "declined", "published"};
    private final int INACTIVE = 0;
    private final int PENDING = 1;
    private final int DECLINED = 2;
    private final int PUBLISHED = 3;


    @Value("${blog.post.title.length.min}")
    private int minLengthTitle;

    @Value("${blog.post.text.length.min}")
    private int minLengthText;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagService tagService, UserService userService, TagToPostService tagToPostService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.userService = userService;
        this.tagToPostService = tagToPostService;
    }


    //Добавить пост
    @Override
    public Post addPost(NewPostRequest newPostRequest, User user) {
        Post post = createNewPost(newPostRequest);
        post.setUser(user);
        post = savePost(post);

        Set<String> tagsName = newPostRequest.getTags();

        Post finalPost = post;
        tagsName.forEach(name -> {
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


    /**
     * Получение списка всех постов в соотвествии с режимом(параметр mode)
     *
     * @param mode   - режим сортировки
     * @param offset - сдвиг страницы
     * @param limit  - кол-во публикаций для 1 страницы
     * @return
     */
    @Override
    public SortedPostsResponse getSortedPosts(String mode, int offset, int limit) {
        SortedPostsResponse sortedPostsResponse = new SortedPostsResponse();

        List<Post> posts = getModePosts(mode, offset, limit);
        List<PostResponse> postResponses = new ArrayList<>();
        posts.forEach(post -> postResponses.add(createPostResponse(post, 1)));

        List<Post> allPosts = postRepository.findAll();


        sortedPostsResponse.setPosts(postResponses);
        sortedPostsResponse.setCount(allPosts.size());

        return sortedPostsResponse;
    }

    /**
     * поиск поста по id
     */
    @Override
    public Post findById(int id) {
        return postRepository.findById(id).orElse(null);
    }

    /**
     * Получение списка постов пользователя соотвествии со статусом (параметр status)
     *
     * @param id     - номер пользователя
     * @param status - статус модерации:
     *               inactive - скрытые, еще не опубликованные is_active = 0;
     *               pending - активные, ожидают утверждения модератором is_active = 1, moderation_status = NEW;
     *               declined - отклоненные по итогам модерации  is_active = 1, moderation_status = DECLINED;
     *               published - принятые по итогам модерации is_active = 1, moderation_status = ACCEPTED;
     * @param offset - сдвиг страницы
     * @param limit  - кол-во публикаций для 1 страницы
     * @return
     */
    @Override
    public SortedPostsResponse getSortedPostsById(int id, String status, int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        int count = 0;
        Pageable pageable = getPageable(offset, limit, Sort.Direction.DESC, PostRepository.POST_TIME);

        if (status.equalsIgnoreCase(STATUS[INACTIVE])) { // скрытые is_active = 0;
            posts = postRepository.findAllByUserIdAndIsActive(id, (byte) 0, pageable);
            count = postRepository.findAllByUserIdAndIsActive(id, (byte) 0).size();

        } else if (status.equalsIgnoreCase(STATUS[PENDING])) { //активные без модераторации is_active = 1, moderation_status = NEW;
            posts = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, (byte) 1, ModerationStatus.NEW, pageable);
            count = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, (byte) 1, ModerationStatus.NEW).size();

        } else if (status.equalsIgnoreCase(STATUS[DECLINED])) { //отклоненные is_active = 1, moderation_status = DECLINED;
            posts = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, (byte) 1, ModerationStatus.DECLINED, pageable);
            count = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, (byte) 1, ModerationStatus.DECLINED).size();

        } else if (status.equalsIgnoreCase(STATUS[PUBLISHED])) { //принятые is_active = 1, moderation_status = ACCEPTED;
            posts = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, (byte) 1, ModerationStatus.ACCEPTED, pageable);
            count = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, (byte) 1, ModerationStatus.ACCEPTED).size();

        }

        //формирование ответа
        List<PostResponse> postResponses = new ArrayList<>();
        posts.forEach(post -> postResponses.add(createPostResponse(post, 1)));

        SortedPostsResponse sortedPostsResponse = new SortedPostsResponse();
        sortedPostsResponse.setPosts(postResponses);
        sortedPostsResponse.setCount(count);

        return sortedPostsResponse;
    }

    @Override
    public PostResponse getPostResponseById(int id) {
        Post post = findById(id);
        if (post.getIsActive() == 1 &&
                post.getModerationStatus() == ModerationStatus.ACCEPTED &&
                post.getTime().isBefore(LocalDateTime.now())) {

            return createPostResponse(post, 2);
        }
        return null;
    }

    @Override
    public void upViewCountOfPost(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        savePost(post);
    }

    /**
     * postResponse на основе post
     *
     * @param post - данные поста, полученные из БД;
     * @param mode - режим сложности для ответа:
     *             1 - простой, без списком комментов, тегов,
     *             2 - усложненный, с комментариями и тегами, вместо анонса полный текст, юзер с фото.
     */
    private PostResponse createPostResponse(Post post, int mode) {
        PostResponse.PostResponseBuilder postBuilder = PostResponse.builder();
        if (mode >= 1) {
            postBuilder.id(post.getId());
            postBuilder.timestamp(post.getTime());
            postBuilder.user(userService.createUserResponse(post.getUser(), 1));
            postBuilder.title(post.getTitle());
            postBuilder.announce(getAnnounce(post.getText()));
            postBuilder.likeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
            postBuilder.dislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
            postBuilder.commentCount(post.getComments().size());
            postBuilder.viewCount(post.getViewCount());
        }
        if (mode >= 2) {
            postBuilder.comments(post.getComments()
                    .stream()
                    .map(this::createCommentResponse)
                    .collect(Collectors.toList()));
            postBuilder.tags(post.getTags()
                    .stream()
                    .map(TagToPost::getTag)
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
            postBuilder.active(post.getIsActive() == 1);
            postBuilder.announce(null);
            postBuilder.text(post.getText());
            postBuilder.commentCount(null);

        }
        return postBuilder.build();
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

    /**
     * Сохранить пост в БД
     *
     * @param post - пост для добавления в БД
     * @return Post - пост, который добавили
     */
    private Post savePost(Post post) {
        return postRepository.save(post);
    }

    /**
     * получение постов в соотвествии с режимом
     *
     * @param mode   - режим сортировки
     * @param offset - сдвиг страницы
     * @param limit  - кол-во публикаций для 1 страницы
     * @return List<Post> - лист отсортированных постов для страницы
     */
    private List<Post> getModePosts(String mode, int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        if (mode.equalsIgnoreCase(this.MODE[RECENT])) { //по дате публикации новые
            Pageable pageable = getPageable(offset, limit, Sort.Direction.DESC, PostRepository.POST_TIME);
            posts = postRepository.findAllByOrderByTimeDesc(pageable);

        } else if (mode.equalsIgnoreCase(this.MODE[BEST])) { //по убыванию лайков
            posts = postRepository.findAll();
            posts.sort((o1, o2) -> {
                long likeO1 = o1.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count();
                long likeO2 = o2.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count();
                if (likeO1 > likeO2) {
                    return -1;
                } else if (likeO1 < likeO2) {
                    return 1;
                }
                return 0;
            });

            posts = getPostsPage(offset, limit, posts);

        } else if (mode.equalsIgnoreCase(this.MODE[POPULAR])) { //по убыванию комментов
            posts = postRepository.findAll();
            posts.sort((o1, o2) -> {
                if (o1.getComments().size() > o2.getComments().size()) {
                    return -1;
                } else if (o1.getComments().size() < o2.getComments().size()) {
                    return 1;
                }
                return 0;
            });

            posts = getPostsPage(offset, limit, posts);

        } else if (mode.equalsIgnoreCase(this.MODE[EARLY])) { //по дате публикации старые
            Pageable pageable = getPageable(offset, limit, Sort.Direction.ASC, PostRepository.POST_TIME);
            posts = postRepository.findAllByOrderByTimeAsc(pageable);
        }
        return posts;
    }

    /**
     * список постов для страницы
     *
     * @param limit  - кол-во публикаций для 1 страницы
     * @param offset - сдвиг страницы
     * @param posts  - список всех постов
     * @return
     */
    private List<Post> getPostsPage(int offset, int limit, List<Post> posts) {
        PagedListHolder page = new PagedListHolder(posts);
        page.setPageSize(limit); // number of items per page
        page.setPage(offset / limit);
        posts = page.getPageList();
        return posts;
    }


    /**
     * получение нового объекта Post из данных поступивших по запросу
     */
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

    /**
     * инициализация полей объекта Post из данных поступивших по запросу
     */
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

    /**
     * формирование CommentResponse на основе PostComment
     *
     * @param postComment
     * @return
     */
    private CommentResponse createCommentResponse(PostComment postComment) {
        CommentResponse.CommentResponseBuilder commentResponseBuilder = CommentResponse.builder();
        commentResponseBuilder.id(postComment.getId());
        commentResponseBuilder.text(postComment.getText());
        commentResponseBuilder.time(postComment.getTime());
        commentResponseBuilder.user(userService.createUserResponse(postComment.getUser(), 2));

        return commentResponseBuilder.build();
    }

    /**
     * Получение записей на стене пользователя.
     *
     * @param id     - ID пользователя, со стены которого требуется получить записи.
     * @param offset - Отступ от начала результирующего списка публикаций.
     * @param limit  - Количество постов, которое нужно вывести.
     * @return - получение результирующего списка с публикациями на стене пользователя;.
     */
    private List<Post> findAllById(int id, int offset, int limit) {
        Pageable pageable = getPageable(offset, limit, Sort.Direction.DESC, PostRepository.POST_TIME);
        return postRepository.findAllById(id, pageable);
    }

    /**
     * получение Pageable
     *
     * @param offset        -  сдвиг страницы
     * @param limit         - кол-во публикаций для 1 страницы
     * @param sortDirection - направление сортировки
     * @param sortField     - поле, по которому идет сортировка
     * @return Pageable obj.
     */
    private Pageable getPageable(int offset, int limit, Sort.Direction sortDirection, String sortField) {
        int pageNumber = offset / limit;
        Sort sort = Sort.by(sortDirection, sortField);
        return PageRequest.of(pageNumber, limit, sort);
    }

    //проверка длины заголовка
    private boolean checkTitle(String title) {
        return title.length() >= minLengthTitle;
    }

    //проверка длины текста
    private boolean checkText(String text) {
        return getAnnounce(text).length() >= minLengthText;
    }
}
