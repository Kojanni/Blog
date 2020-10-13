package com.kochetkova.controller;

import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.request.NewVoteRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.api.response.SortedPostsResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.User;
import com.kochetkova.service.PostService;
import com.kochetkova.service.PostVoteService;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@Transactional
@RequestMapping("/api/post")
public class ApiPostController {
    private UserService userService;
    private PostService postService;
    private PostVoteService postVoteService;

    @Autowired
    public ApiPostController(UserService userService, PostService postService, PostVoteService postVoteService) {
        this.userService = userService;
        this.postService = postService;
        this.postVoteService = postVoteService;
    }

    /**
     * добавляет пост
     * POST запрос /api/post
     * Авторизация: требуется
     *
     * @param newPostRequest - данные добавляемого поста
     * @return 200 в любом случае + resultError:
     * true - все добавлено,
     * false + error - есть ошибки в данных.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultErrorResponse> addPosts(Principal principal, @RequestBody NewPostRequest newPostRequest) {

        //Авторизация есть?
        if (principal == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserByEmail(principal.getName());
        ResultErrorResponse resultError = new ResultErrorResponse();
        ErrorResponse error = postService.checkAddedPost(newPostRequest);
        if (!error.isPresent()) {
            resultError.setResult(true);
            postService.addPost(newPostRequest, user);
        } else {
            resultError.setErrors(error);
        }
        return new ResponseEntity<>(resultError, HttpStatus.OK);
    }

    /**
     * Вывод списко постов
     * GET запрос /api/post
     * Должны выводиться только активные (is_active = 1),
     * утверждённые модератором (moderation_status = ACCEPTED) посты
     * с датой публикации не позднее текущего момента.
     * Авторизация: не требуется
     *
     * @param mode   - режим вывода (сортровка)
     * @param offset - сдвиг от 0 для постграничного вывода
     * @param limit  - количество постов, которое нужно вывести
     * @return 200 В любом случае
     * Посты воращаются в формате SortedPostsResponse, при отсутствии постов вернуть пустой класс
     */
    @GetMapping("")
    public ResponseEntity<SortedPostsResponse> getPostsList(@RequestParam String mode,
                                                            @RequestParam int offset,
                                                            @RequestParam int limit) {

        SortedPostsResponse sortedPosts = postService.getSortedPosts(mode, offset, limit);

        return new ResponseEntity<>(sortedPosts, HttpStatus.OK);
    }

    /**
     * Поиск постов
     * Get запрос /api/post/search
     * Возвращает посты, соотвествующие поисковому запросу
     * Авторизация: не требуется
     *
     * @param query - строка поискового запроса
     * @return 200 В любом случае
     * Посты возвращаются в формате SortedPostsResponse, при отсутствии постов - пустой класс
     */
    @GetMapping("/search")
    public ResponseEntity<SortedPostsResponse> getPostsWithQuery(@RequestParam("query") String query,
                                                                 @RequestParam("offset") int offset,
                                                                 @RequestParam("limit") int limit) {
        SortedPostsResponse sortedPosts = postService.getSortedPostsByQuery(query, offset, limit);

        return new ResponseEntity<>(sortedPosts, HttpStatus.OK);
    }

    /**
     * Список постов за указанную дату
     * GET запрос /api/post/byDate
     *
     * @param date   - дата
     * @param offset - сдвиг от 0 для постграничного вывода
     * @param limit  - количество постов, которое нужно вывести
     * @return 200 В любом случае
     * Посты воращаются в формате SortedPostsResponse, при отсутствии постов вернуть пустой класс
     */
    @GetMapping("/byDate")
    public ResponseEntity<SortedPostsResponse> getPostByDate(@RequestParam("date") String date,
                                                             @RequestParam("offset") int offset,
                                                             @RequestParam("limit") int limit) {

        SortedPostsResponse sortedPostsResponse = postService.getSortedPostsByDate(date, offset, limit);

        return new ResponseEntity<>(sortedPostsResponse, HttpStatus.OK);
    }

    /**
     * Список постов по тэгу
     * GET запрос /api/post/byTag
     *
     * @param tag    - тег
     * @param offset - сдвиг от 0 для постграничного вывода
     * @param limit  - количество постов, которое нужно вывести
     * @return 200 В любом случае
     * Посты воращаются в формате SortedPostsResponse, при отсутствии постов вернуть пустой класс
     */
    @GetMapping("/byTag")
    public ResponseEntity<SortedPostsResponse> getPostsByTag(@RequestParam("tag") String tag,
                                                             @RequestParam("offset") int offset,
                                                             @RequestParam("limit") int limit) {
        SortedPostsResponse sortedPostsResponse = postService.getSortedPostsByTag(tag, offset, limit);

        return new ResponseEntity<>(sortedPostsResponse, HttpStatus.OK);
    }

    /**
     * Список постов на модерацию
     * GET запрос /api/post/moderation
     * Должны выводиться только активные (is_active = 1) посты
     * Авторизация: требуется
     *
     * @param status - статус модерации:
     *               new - новые, необходжима модерация,
     *               declined - отклоненные пользователем,
     *               accepted - утвержденные пользователем,
     * @param offset - сдвиг от 0 для постграничного вывода
     * @param limit  - количество постов, которое нужно вывести
     * @return 200 В любом случае
     * Посты воращаются в формате SortedPostsResponse, при отсутствии постов вернуть пустой класс
     */
    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<SortedPostsResponse> getPostForModeration(Principal principal,
                                                                    @RequestParam("status") String status,
                                                                    @RequestParam("offset") int offset,
                                                                    @RequestParam("limit") int limit) {
        //Авторизация есть?
        if (principal == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserByEmail(principal.getName());
        SortedPostsResponse sortedPostsResponse = postService.getSortedPostsForModeration(user, status, offset, limit);

        return new ResponseEntity<>(sortedPostsResponse, HttpStatus.OK);
    }

    /**
     * выводит посты, которые создал текущий пользователь(соотвествующий id)
     * GET /api/post/my
     * Авторизация: требуется
     *
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param status - статус модерации:
     *               inactive - скрытые, ещё не опубликованы (is_active = 0)
     *               pending - активные, ожидают утверждения модератором (is_active = 1, moderation_status = NEW)
     *               declined - отклонённые (is_active = 1, moderation_status = DECLINED)
     *               published - опубликованные (is_active = 1, moderation_status = ACCEPTED)
     * @return 200 Status при любом ответе
     * Посты воращаются в формате SortedPostsResponse, при отсутствии постов вернуть пустой класс
     */
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<SortedPostsResponse> getUserPosts(Principal principal,
                                                            @RequestParam String status,
                                                            @RequestParam int offset,
                                                            @RequestParam int limit) {

        //Авторизация есть?
        if (principal == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserByEmail(principal.getName());
        SortedPostsResponse sortedPostsResponse = postService.getSortedPostsById(user.getId(), status, offset, limit);

        return new ResponseEntity<>(sortedPostsResponse, HttpStatus.OK);
    }

    /**
     * Получение поста /api/post/{id}
     * Авторизация: не требуется
     * Метод выводит данные конкретного поста для отображения на странице поста.
     * Выводит пост в любом случае, если пост активен (is_active = 1),
     * принят модератором ( moderation_status = ACCEPTED),
     * время его публикации <= текущему времени
     *
     * @param id - номер поста
     * @return 404 - если пост не найден,
     * 200 + PostResponse(с комментариями и тегами - режим 2)
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(Principal principal, @PathVariable int id) {

        Post post = postService.findById(id);
        PostResponse postResponse = postService.getPostResponseByPost(post);

        if (postResponse == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        if (principal != null) { //auth user check and add view to post
            postService.addViewToPost(post, userService.findUserByEmail(principal.getName()));
        }

        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    /**
     * Редактирование поста
     * PUT запрос /api/post/{id}
     * Авторизация: требуется
     *
     * @param id - номер редактируемого поста
     * @return resultError
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultErrorResponse> editPost(Principal principal, @PathVariable("id") int id, @RequestBody NewPostRequest newPostRequest) {
        //Авторизация есть?
        if (principal == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserByEmail(principal.getName());
        ResultErrorResponse resultError = new ResultErrorResponse();
        ErrorResponse error = postService.checkAddedPost(newPostRequest);
        if (!error.isPresent()) {
            resultError.setResult(true);
            postService.putPost(id, newPostRequest, user);
        } else {
            resultError.setErrors(error);
        }
        return new ResponseEntity<>(resultError, HttpStatus.OK);
    }

    /**
     * добавление лайка
     * POST /api/post/like
     * Авторизация: требуется
     *
     * @param newVoteRequest - номер поста
     * @return result   = true - если лайк прошел,
     * = false - если нет
     */
    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultErrorResponse> postLike(HttpServletRequest request, Principal principal, @RequestBody NewVoteRequest newVoteRequest) {
//todo: проверить обновление возвращаемого поста
        //Авторизация есть?
        if (principal == null &&
                userService.findAuthSession(request.getRequestedSessionId())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserByEmail(principal.getName());
        Post post = postService.findById(newVoteRequest.getPostId());
        ResultErrorResponse resultErrorResponse = postVoteService.addLike(post, user);

        return new ResponseEntity<>(resultErrorResponse, HttpStatus.OK);
    }

    /**
     * добавление дизлайка
     * POST /api/post/dislike
     * Авторизация: требуется
     *
     * @param newVoteRequest - номер поста
     * @return result   = true - если лайк прошел,
     * = false - если нет
     */
    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultErrorResponse> postDislike(HttpServletRequest request, Principal principal, @RequestBody NewVoteRequest newVoteRequest) {
//todo: проверить обновление возвращаемого поста
        //Авторизация есть?
        if (principal == null &&
                userService.findAuthSession(request.getRequestedSessionId())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserByEmail(principal.getName());
        Post post = postService.findById(newVoteRequest.getPostId());
        ResultErrorResponse resultErrorResponse = postVoteService.addDislike(post, user);

        return new ResponseEntity<>(resultErrorResponse, HttpStatus.OK);
    }
}
