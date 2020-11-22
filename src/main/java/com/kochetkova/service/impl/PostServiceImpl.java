package com.kochetkova.service.impl;

import com.kochetkova.api.request.ModerationPostRequest;
import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.response.*;
import com.kochetkova.model.*;
import com.kochetkova.service.impl.enums.*;
import com.kochetkova.repository.PostRepository;
import com.kochetkova.service.*;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private TagService tagService;
    private UserService userService;
    private TagToPostService tagToPostService;
    private SettingsService settingsService;

    private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatterYears = DateTimeFormatter.ofPattern("yyyy");
    private final String separator = File.separator;

    @Value("${blog.post.title.length.min}")
    private int minLengthTitle;

    @Value("${blog.post.text.length.min}")
    private int minLengthText;

    @Value("${photo.postImg.path}")
    private String imagePath;

    @Value("${photo.postImg.nameSize}")
    private int postImgNameSize;

    @Value("${photo.postImg.subfolderSize}")
    private int postImgSubfolderSize;

    private int moderatorId = 1;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagService tagService, UserService userService, TagToPostService tagToPostService, SettingsService settingsService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.userService = userService;
        this.tagToPostService = tagToPostService;
        this.settingsService = settingsService;
    }

    /**
     * поиск поста по id
     */
    @Override
    public Post findById(int id) {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    /**
     * Список всех постов:
     * только активные (is_active = 1),
     * утверждённые модератором ( moderation_status = ACCEPTED)
     * с датой публикации не позднее текущего момента.
     *
     * @return List<Post>
     */
    @Override
    public List<Post> findAll() {
        return postRepository.findAllByIsActiveAndModerationStatusAndTimeBefore((byte) 1, ModerationStatus.ACCEPTED, LocalDateTime.now());
    }

    /**
     * добавляет пост
     *
     * @param newPostRequest - данные добавляемого поста
     * @param user           - пользователь, который добавляет пост
     * @return post - добавленный пост
     */
    @Override
    @Transactional
    public Post addPost(NewPostRequest newPostRequest, User user) {
        Post post = createNewPost(newPostRequest);
        post.setUser(user);
        post = savePost(post);

        saveTag(newPostRequest, post);
        return post;
    }

    private void saveTag(NewPostRequest newPostRequest, Post post) {
        newPostRequest.getTags().forEach(name -> {
            Tag tag = tagService.findByTag(name);
            if (tag == null) {
                tag = tagService.save(name);
            }
            tagToPostService.save(tag, post);
        });
    }


    /**
     * Изменить данные поста
     *
     * @param id             - номер поста
     * @param newPostRequest - новые данные для поста
     * @return Post - пост с новыми данными
     */
    @Override
    public Post putPost(int id, NewPostRequest newPostRequest, User user) {
        Post post = findById(id);
        editPost(post, newPostRequest, user);
        editTag(newPostRequest, post);
        return savePost(post);
    }

    private void editTag(NewPostRequest newPostRequest, Post post) {
        tagToPostService.deleteByPost(post);
        newPostRequest.getTags().forEach(name -> {
            Tag tag = tagService.findByTag(name);
            if (tag == null) {
                tag = tagService.save(name);
            }
            tagToPostService.save(tag, post);
        });
    }

    //Проверка данных добавляемого поста
    @Override
    public ErrorResponse checkAddedPost(NewPostRequest newPostRequest) {
        ErrorResponse.ErrorResponseBuilder errorBuilder = ErrorResponse.builder();
        if (!checkText(newPostRequest.getText())) {
            errorBuilder.text("Текст публикации слишком короткий.\nМинимальная длина: " + minLengthText + " символов");
        }
        if (!checkTitle(newPostRequest.getTitle())) {
            errorBuilder.title("Заголовок не установлен или короткий");
        }

        return errorBuilder.build();
    }


    /**
     * Получение списка всех постов в соотвествии с режимом(параметр mode)
     * Должны выводиться только активные (is_active = 1),
     * утверждённые модератором (moderation_status = ACCEPTED) посты
     * с датой публикации не позднее текущего момента.
     *
     * @param mode   - режим сортировки
     * @param offset - сдвиг страницы
     * @param limit  - кол-во публикаций для 1 страницы
     */
    @Override
    public SortedPostsResponse getSortedPosts(String mode, int offset, int limit) {
        SortedPostsResponse sortedPostsResponse = new SortedPostsResponse();

        List<Post> posts = getModePosts(mode, offset, limit);
        List<PostResponse> postResponses = new ArrayList<>();
        posts.forEach(post -> postResponses.add(createPostResponse(post, ModePostInfo.SHORTINFO_COUNT)));

        int allPostsCount = postRepository.countByIsActiveAndModerationStatusAndTimeBefore((byte) 1, ModerationStatus.ACCEPTED, LocalDateTime.now());


        sortedPostsResponse.setPosts(postResponses);
        sortedPostsResponse.setCount(allPostsCount);

        return sortedPostsResponse;
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
     */
    @Override
    public SortedPostsResponse getSortedPostsById(int id, String status, int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        int count = 0;
        Pageable pageable = getPageable(offset, limit, Sort.Direction.DESC, PostRepository.POST_TIME);

        if (status.equalsIgnoreCase(Status.INACTIVE.getName())) { // скрытые is_active = 0;
            posts = postRepository.findAllByUserIdAndIsActive(id, ActiveStatus.INACTIVE.getValue(), pageable);
            count = postRepository.findAllByUserIdAndIsActive(id, ActiveStatus.INACTIVE.getValue()).size();

        } else if (status.equalsIgnoreCase(Status.PENDING.getName())) { //активные без модераторации is_active = 1, moderation_status = NEW;
            posts = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, ActiveStatus.ACTIVE.getValue(), ModerationStatus.NEW, pageable);
            count = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, ActiveStatus.ACTIVE.getValue(), ModerationStatus.NEW).size();

        } else if (status.equalsIgnoreCase(Status.DECLINED.getName())) { //отклоненные is_active = 1, moderation_status = DECLINED;
            posts = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, ActiveStatus.ACTIVE.getValue(), ModerationStatus.DECLINED, pageable);
            count = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, ActiveStatus.ACTIVE.getValue(), ModerationStatus.DECLINED).size();

        } else if (status.equalsIgnoreCase(Status.PUBLISHED.getName())) { //принятые is_active = 1, moderation_status = ACCEPTED;
            posts = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, ActiveStatus.ACTIVE.getValue(), ModerationStatus.ACCEPTED, pageable);
            count = postRepository.findAllByUserIdAndIsActiveAndModerationStatus(id, ActiveStatus.ACTIVE.getValue(), ModerationStatus.ACCEPTED).size();

        }

        //формирование ответа
        List<PostResponse> postResponses = new ArrayList<>();
        posts.forEach(post -> postResponses.add(createPostResponse(post, ModePostInfo.SHORTINFO_COUNT)));

        SortedPostsResponse sortedPostsResponse = new SortedPostsResponse();
        sortedPostsResponse.setPosts(postResponses);
        sortedPostsResponse.setCount(count);

        return sortedPostsResponse;
    }

    /**
     * Формирование ответа PostResponse для запроса getPost
     *
     * @param postId    - id поста
     * @param userEmail - email пользователя, =null - если не авторизован
     * @return PostResponse
     */
    @Override
    public PostResponse getPostResponse(int postId, String userEmail) {
        User user = userEmail != null ? userService.findUserByEmail(userEmail) : null;

        if ( user == null || !checkPostUserOnModeratorStatus(user)) {
            addViewToPost(postId, user);
        }

        PostResponse postResponse = null;
        Post post = findById(postId);
        if (checkPostForMakeVisible(post) || (userEmail != null && post.getUser() == user)) {
            postResponse = getPostResponseByPost(post);
        }

        return postResponse;
    }

    /**
     * Добавить просмотр посту
     *
     * @param postId - пост id
     * @param user - пользователь (=null есть пользователь не авторизован)
     */
    private void addViewToPost(int postId, User user) {
        postRepository.incrementPostViewCountIfNotUser(postId, user);
    }

    /**
     * Проверяет: Пользователь является модератором
     */
    private boolean checkPostUserOnModeratorStatus(User user) {
        return user != null && user.getIsModerator() == 1;
    }

    /**
     * получение PostResponse на основе данных о посте,
     * если пост активен, принят и опубликован до текущего времени
     *
     * @param post - данные о посте
     */
    @Override
    public PostResponse getPostResponseByPost(Post post) {
        return createPostResponse(post, ModePostInfo.INFO_COUNT_COMMENT_TAG);
    }

    private boolean checkPostForMakeVisible(Post post) {
        return post.getIsActive() == 1 &&
                post.getModerationStatus() == ModerationStatus.ACCEPTED &&
                post.getTime().isBefore(LocalDateTime.now());
    }

    /**
     * получение PostResponse на основе данных о посте,
     * если пост активен, принят и опубликован до текущего времени
     * или
     * если пост запрашивает его автор
     *
     * @param post - данные о посте
     * @param user - запрашивающий пользователь
     */
    @Override
    public PostResponse getPostResponseByPost(Post post, User user) {
        if (checkPostForMakeVisible(post) || post.getUser() == user) {
            return createPostResponse(post, ModePostInfo.INFO_COUNT_COMMENT_TAG);
        }
        return null;
    }


    /**
     * Календарь(количество публикаций)
     *
     * @param year - год, за который необходимо подсчитать количество,
     *             если не задан, то за все года
     * @return CalendarResponse - год(года) и список дата-количество
     */
    @Override
    public CalendarResponse getPostsCountByYear(Integer year) {
        List<Post> postByYears;

        if (year == null) {
            postByYears = findAll();
        } else {
            LocalDateTime timeStart = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
            LocalDateTime timeEnd = timeStart.plusYears(1);
            postByYears = postRepository.findAllByTimeBetweenAndIsActiveAndModerationStatusAndTimeBefore(timeStart, timeEnd, ActiveStatus.ACTIVE.getValue(), ModerationStatus.ACCEPTED, LocalDateTime.now());
        }

        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setPosts(getPostCountOnDay(postByYears));
        calendarResponse.setYears(getYears(postByYears));

        return calendarResponse;
    }

    /**
     * Подсчитывает количество публикаций в день для списка постов
     *
     * @param posts - списк постов
     * @return postsCount - количество публикаций в день
     */
    private Map<String, Integer> getPostCountOnDay(List<Post> posts) {
        Map<String, Integer> postsCount = new TreeMap<>();
        posts.forEach(post -> {
            String date = formatterDate.format(post.getTime());
            postsCount.computeIfPresent(date, (key, val) -> val + 1);
            postsCount.putIfAbsent(date, 1);
        });
        return postsCount;
    }

    /**
     * Создает список годов, в которых есть публикации
     *
     * @param posts - список постов
     * @return years - года
     */
    private List<Integer> getYears(List<Post> posts) {
        List<Integer> years = new ArrayList<>();
        posts.forEach(post -> {
            int year = Integer.parseInt(formatterYears.format(post.getTime()));
            if (!years.contains(year)) {
                years.add(year);
            }
        });
        return years;
    }

    /**
     * Поиск постов. Возвращает посты, соотвествующие поисковому запросу
     *
     * @param query  - строка поискового запроса
     * @param offset - сдвиг страницы
     * @param limit  - кол-во публикаций для 1 страницы
     * @return SortedPostsResponse - общее количетво и список постов List<Post>
     */
    @Override
    public SortedPostsResponse getSortedPostsByQuery(String query, int offset, int limit) {
        Pageable pageable = getPageable(offset, limit, Sort.Direction.DESC, PostRepository.POST_TIME);
        LocalDateTime time = LocalDateTime.now();
        byte isActive = 1;
        ModerationStatus moderationStatus = ModerationStatus.ACCEPTED;
        int count;
        List<Post> posts;

        if (query == null) {
            posts = postRepository.findAllByIsActiveAndModerationStatusAndTimeBefore(isActive, moderationStatus, time, pageable);
            count = postRepository.countAllByIsActiveAndModerationStatusAndTimeBefore(isActive, moderationStatus, time);
        } else {
            posts = postRepository
                    .findAllByTextContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBeforeOrTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(query, isActive, moderationStatus, time, query, isActive, moderationStatus, time, pageable);
            count = postRepository
                    .countByTextContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBeforeOrTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(query, isActive, moderationStatus, time, query, isActive, moderationStatus, time);
        }
        SortedPostsResponse sortedPostsResponse = new SortedPostsResponse();
        sortedPostsResponse.setCount(count);
        sortedPostsResponse.setPosts(posts.stream().map(post -> createPostResponse(post, ModePostInfo.SHORTINFO_COUNT)).collect(Collectors.toList()));
        return sortedPostsResponse;
    }

    /**
     * Список постов за указанную дату
     *
     * @param dateString - дата
     * @param offset     - сдвиг от 0 для постграничного вывода
     * @param limit      - количество постов, которое нужно вывести
     * @return SortedPostsResponse - общее количетво и список постов List<Post>
     */
    @Override
    public SortedPostsResponse getSortedPostsByDate(String dateString, int offset, int limit) {
        Pageable pageable = getPageable(offset, limit, Sort.Direction.DESC, PostRepository.POST_TIME);

        LocalDateTime date = LocalDate.parse(dateString, formatterDate).atStartOfDay();

        List<Post> posts = postRepository.findAllByTimeBetweenAndIsActiveAndModerationStatusAndTimeBefore(date, date.plusDays(1), ActiveStatus.ACTIVE.getValue(), ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
        int count = postRepository.countByTimeBetweenAndIsActiveAndModerationStatusAndTimeBefore(date, date.plusDays(1), ActiveStatus.ACTIVE.getValue(), ModerationStatus.ACCEPTED, LocalDateTime.now());

        SortedPostsResponse sortedPostsResponse = new SortedPostsResponse();
        sortedPostsResponse.setCount(count);
        sortedPostsResponse.setPosts(posts.stream().map(post -> createPostResponse(post, ModePostInfo.SHORTINFO_COUNT)).collect(Collectors.toList()));
        return sortedPostsResponse;
    }

    /**
     * Список постов по тэгу
     *
     * @param tagString - тег
     * @param offset    - сдвиг от 0 для постграничного вывода
     * @param limit     - количество постов, которое нужно вывести
     * @return SortedPostsResponse - общее количетво и список постов List<Post>
     */
    @Override
    public SortedPostsResponse getSortedPostsByTag(String tagString, int offset, int limit) {

        List<Post> posts = tagService.findByTag(tagString)
                .getPosts()
                .stream()
                .map(TagToPost::getPost)
                .filter(post -> post.getIsActive() == 1)
                .filter(post -> post.getModerationStatus() == ModerationStatus.ACCEPTED)
                .filter(post -> post.getTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Post::getTime).reversed())
                .collect(Collectors.toList());

        int count = posts.size();
        posts = getPostsPage(offset, limit, posts);

        SortedPostsResponse sortedPostsResponse = new SortedPostsResponse();
        sortedPostsResponse.setCount(count);
        sortedPostsResponse.setPosts(posts.stream().map(post -> createPostResponse(post, ModePostInfo.SHORTINFO_COUNT)).collect(Collectors.toList()));
        return sortedPostsResponse;
    }

    /**
     * Список постов на модерацию
     *
     * @param user   - авторизованный пользователь (модератор)
     * @param status - статус модерации:
     *               new - новые, необходжима модерация,
     *               declined - отклоненные модератором,
     *               accepted - утвержденные модератором,
     * @param offset - сдвиг от 0 для постграничного вывода
     * @param limit  - количество постов, которое нужно вывести
     * @return SortedPostsResponse - общее количетво и список постов List<Post>
     */
    @Override
    public SortedPostsResponse getSortedPostsForModeration(User user, String status, int offset, int limit) {

        Pageable pageable = getPageable(offset, limit, Sort.Direction.DESC, PostRepository.POST_TIME);
        ModerationStatus moderationStatus = ModerationStatus.NEW;
        if (status.equalsIgnoreCase("declined")) { //отклоненные модератором
            moderationStatus = ModerationStatus.DECLINED;

        } else if (status.equalsIgnoreCase("accepted")) { // утвержденные модератором
            moderationStatus = ModerationStatus.ACCEPTED;
        }

        List<Post> posts = postRepository.findAllByModeratorIdAndIsActiveAndModerationStatus(user.getId(), ActiveStatus.ACTIVE.getValue(), moderationStatus, pageable);
        int count = postRepository.countByModeratorIdAndIsActiveAndModerationStatus(user.getId(), ActiveStatus.ACTIVE.getValue(), moderationStatus);

        //формирование ответа
        SortedPostsResponse sortedPostsResponse = new SortedPostsResponse();
        sortedPostsResponse.setPosts(posts.stream().map(post -> createPostResponse(post, ModePostInfo.SHORTINFO_COUNT)).collect(Collectors.toList()));
        sortedPostsResponse.setCount(count);

        return sortedPostsResponse;
    }

    /**
     * Модерация поста
     *
     * @param moderationPostRequest - данные для изменения статуса поста
     * @return true - если все изменено,
     * false - если по какой-то причине изменить статус не удалось
     */
    @Override
    public boolean changeModerationStatus(ModerationPostRequest moderationPostRequest) {
        Post post = findById(moderationPostRequest.getPostId());
        if (moderationPostRequest.getDecision().equalsIgnoreCase("accept")) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatus.DECLINED);
        }
        return postRepository.save(post) != null;
    }

    /**
     * СТатистика для текущего пользователя
     *
     * @param user - текущий пользователь (not null)
     * @return StatisticsResponse - общие количества параметров для всех публикаций, у который он является автором и доступные для чтения
     */
    @Override
    public StatisticsResponse getUserStatistics(User user) {
        List<Post> posts = postRepository.findAllByUserIdAndIsActive(user.getId(), ActiveStatus.ACTIVE.getValue());

        return getStatisticsResponse(posts);
    }

    /**
     * Статистика по всему блогу
     *
     * @return StatisticsResponse - общие количества параметров для всех публикаций, у который он является автором и доступные для чтения
     */
    @Override
    public StatisticsResponse getStatistics() {
        List<Post> posts = findAll();

        return getStatisticsResponse(posts);
    }

    /**
     * Загрузка изображений
     * <p>
     * Авторизация: требуется
     *
     * @param image - картинка для поста
     * @return Метод возвращает путь до изображения
     */
    @Override
    public String savePostImage(MultipartFile image) {

        if (!image.isEmpty()) {
            String extension = FilenameUtils.getExtension(image.getOriginalFilename());
            if (!(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png"))) {
                return null;
            }
            String fullPath = imagePath + separator + getString(postImgSubfolderSize) + separator + getString(postImgSubfolderSize) + separator + getString(postImgSubfolderSize) + separator + getNumber(postImgNameSize) + "." + extension;

            File file = new File(fullPath);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            while (file.exists()) {
                fullPath = file.getParent() + getNumber(postImgNameSize) + "." + extension;
                file = new File(fullPath);
            }

            try {
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                ImageIO.write(bufferedImage, extension, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "/" + fullPath.replace("\\", "/");
        }
        return null;
    }

    /**
     * получение строки из букв a-z указанной длины
     *
     * @param length - длина
     */
    private String getString(int length) {
        return (new Random()).ints(97, 122)
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    /**
     * получение строки из цифр 0-9 указанной длины
     *
     * @param length - длина
     */
    private String getNumber(int length) {
        return (new Random()).ints(48, 57)
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    /**
     * Полученипе статистики на основе списка постов
     *
     * @param posts - список постов
     * @return StatisticsResponse - статистика
     */
    private StatisticsResponse getStatisticsResponse(List<Post> posts) {
        StatisticsResponse.StatisticsResponseBuilder statisticsResponseBuilder = StatisticsResponse.builder();

        statisticsResponseBuilder.postsCount(posts.size());
        statisticsResponseBuilder.likesCount(posts.stream()
                .map(Post::getVotes)
                .map(postVotes -> postVotes.stream().map(PostVote::getValue).filter(b -> b == (byte) 1).count())
                .map(Long::intValue)
                .reduce(0, Integer::sum));

        statisticsResponseBuilder.dislikesCount(posts.stream()
                .map(Post::getVotes)
                .map(postVotes -> postVotes.stream().map(PostVote::getValue).filter(b -> b == (byte) -1).count())
                .map(Long::intValue)
                .reduce(0, Integer::sum));
        statisticsResponseBuilder.viewsCount(posts.stream()
                .map(Post::getViewCount)
                .reduce(0, Integer::sum));
        statisticsResponseBuilder.firstPublication(posts.stream()
                .map(Post::getTime)
                .min(LocalDateTime::compareTo)
                .get());

        return statisticsResponseBuilder.build();
    }

    /**
     * postResponse на основе post
     *
     * @param post - данные поста, полученные из БД;
     * @param mode - режим сложности для ответа:
     *             1 - простой, без списком комментов, тегов,
     *             2 - усложненный, с комментариями и тегами, вместо анонса полный текст.
     */
    private PostResponse createPostResponse(Post post, ModePostInfo mode) {
        PostResponse.PostResponseBuilder postBuilder = PostResponse.builder();
        postBuilder.id(post.getId());
        postBuilder.timestamp(post.getTime());
        postBuilder.title(post.getTitle());
        postBuilder.likeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
        postBuilder.dislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
        postBuilder.viewCount(post.getViewCount());

        if (mode.equals(ModePostInfo.SHORTINFO_COUNT)) {
            postBuilder.user(userService.createUserResponse(post.getUser(), ModeUserInfo.ID_NAME));
            postBuilder.announce(html2text(post.getText()));
            postBuilder.commentCount(post.getComments().size());
        }

        if (mode.equals(ModePostInfo.INFO_COUNT_COMMENT_TAG)) {
            postBuilder.user(userService.createUserResponse(post.getUser(), ModeUserInfo.ID_NAME));

            postBuilder.text(post.getText());

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
        }
        return postBuilder.build();
    }

    //текст поста без тегов и форматирования
    private String html2text(String html) {
        return Jsoup.parse(html).text();
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
     * Должны выводиться только активные (is_active = 1),
     * утверждённые модератором (поле moderation_status = ACCEPTED) посты
     * с датой публикации не позднее текущего момента.
     *
     * @param mode   - режим сортировки
     * @param offset - сдвиг страницы
     * @param limit  - кол-во публикаций для 1 страницы
     * @return List<Post> - лист отсортированных постов для страницы
     */
    private List<Post> getModePosts(String mode, int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        Pageable pageable = getPageable(offset, limit, Sort.Direction.DESC, PostRepository.POST_TIME);

        if (mode.equalsIgnoreCase(Mode.RECENT.getName())) { //по дате публикации новые
            posts = postRepository.findAllByIsActiveAndModerationStatusAndTimeBeforeOrderByTimeDesc((byte) 1, ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);

        } else if (mode.equalsIgnoreCase(Mode.BEST.getName())) { //по убыванию лайков
            posts = postRepository.findAllOrderByLikes((byte) 1, ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);

        } else if (mode.equalsIgnoreCase(Mode.POPULAR.getName())) { //по убыванию комментов
            posts = postRepository.findAllOrderByComments((byte) 1, ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);

        } else if (mode.equalsIgnoreCase(Mode.EARLY.getName())) { //по дате публикации старые
            posts = postRepository.findAllByIsActiveAndModerationStatusAndTimeBeforeOrderByTimeAsc((byte) 1, ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
        }

        return posts;
    }

    /**
     * список постов для страницы
     *
     * @param limit  - кол-во публикаций для 1 страницы
     * @param offset - сдвиг страницы
     * @param posts  - список всех постов
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

        Post post = new Post();
        post.setTime(checkTime(newPostRequest));
        post.setTitle(newPostRequest.getTitle());
        post.setText(newPostRequest.getText());
        post.setViewCount(0);
        //todo: кто модератор?????
        post.setModerator(userService.findUserById(moderatorId));

        //проверка глобальных настроек сайта
        if (!settingsService.getSettings().isPostPremoderation()) {
            post.setIsActive((byte) 1);
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setIsActive(newPostRequest.getActive());
            post.setModerationStatus(ModerationStatus.NEW);
        }
        return post;
    }

    /**
     * Проверка времени
     * Время публикации поста также должно проверяться:
     * в случае, если время публикации раньше текущего времени,
     * оно должно автоматически становиться текущим.
     * Если позже текущего - необходимо устанавливать введенное значение.
     *
     * @param newPostRequest - данные для добавления поста
     * @return проверенное время: если время публикации раньше текущего времени - текущее,
     * позже - введенное значение.
     */
    private LocalDateTime checkTime(NewPostRequest newPostRequest) {
        LocalDateTime time = newPostRequest.getTimestamp();
        LocalDateTime timeNow = LocalDateTime.now();
        if (time.isBefore(timeNow)) {
            time = timeNow;
        }
        return time;
    }

    /**
     * инициализация полей объекта Post из данных поступивших по запросу
     */
    @Override
    public void getPostToNewPostRequest(NewPostRequest newPostRequest, Post post) {

        post.setTime(checkTime(newPostRequest));
        post.setIsActive(newPostRequest.getActive());
        post.setTitle(newPostRequest.getTitle());
        post.setText(newPostRequest.getText());
        post.setViewCount(0);
        post.setModerationStatus(ModerationStatus.NEW);
    }

    /**
     * формирование CommentResponse на основе PostComment
     *
     * @param postComment - данные для комментария к посту
     */
    private CommentResponse createCommentResponse(PostComment postComment) {
        CommentResponse.CommentResponseBuilder commentResponseBuilder = CommentResponse.builder();
        commentResponseBuilder.id(postComment.getId());
        commentResponseBuilder.text(postComment.getText());
        commentResponseBuilder.time(postComment.getTime());
        commentResponseBuilder.user(userService.createUserResponse(postComment.getUser(), ModeUserInfo.ID_NAME_PHOTO));

        return commentResponseBuilder.build();
    }

    private void editPost(Post post, NewPostRequest newPostRequest, User user) {
        LocalDateTime current = LocalDateTime.now();
        if (newPostRequest.getTimestamp().isBefore(current)) {
            post.setTime(current);
        } else {
            post.setTime(newPostRequest.getTimestamp());
        }

        post.setIsActive(newPostRequest.getActive());
        post.setTitle(newPostRequest.getTitle());
        post.setText(newPostRequest.getText());

        if (user.getIsModerator() != 1) {
            post.setModerationStatus(ModerationStatus.NEW);
        }
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
        return html2text(text).length() >= minLengthText;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Post not found in DB")
    public class PostNotFoundException extends RuntimeException {
        public PostNotFoundException(int id) {
        }
    }
}
