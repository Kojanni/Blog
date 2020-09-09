package com.kochetkova.controller;

import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.request.NewVoteRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.api.response.SortedPostsResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.PostVote;
import com.kochetkova.model.User;
import com.kochetkova.service.PostService;
import com.kochetkova.service.PostVoteService;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
     *
     * @param newPostRequest - данные добавляемого поста
     * @return 200 в любом случае + resultError:
     * true - все добавлено,
     * false + error - есть ошибки в данных.
     */
    @PostMapping("")
    public ResponseEntity<ResultErrorResponse> addPosts(HttpServletRequest request, @RequestBody NewPostRequest newPostRequest) {

        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

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
     *
     * @param query - строка поискового запроса
     * @return 200 В любом случае
     * Посты возвращаются в формате SortedPostsResponse, при отсутствии постов - пустой класс
     */
    @GetMapping("/search")
    public ResponseEntity<Object> getPostsWithQuery(@RequestParam("query") String query,
                                                    @RequestParam("offset") int offset,
                                                    @RequestParam("limit") int limit) {
        // todo
        SortedPostsResponse sortedPosts = postService.getSortedPostsByQuery(query, offset, limit);

        return new ResponseEntity<>(sortedPosts, HttpStatus.OK);
    }

    /**
     * Получение поста /api/post/{id}
     *
     * @param id - номер поста
     * @return 404 - если пост не найден,
     * 200 + PostResponse(с комментариями и тегами - режим 2)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getPost(HttpServletRequest request, @PathVariable int id) {
        Post post = postService.findById(id);

        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);


        if (!(user.getIsModerator() == 1
                || (post.getUser().getId() == user.getId() && user.getIsModerator() != 1))) {
            postService.upViewCountOfPost(post);
        }
        PostResponse postResponse = postService.getPostResponseById(id);
        if (postResponse == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

//    @GetMapping("/byDate")
//    public ResponseEntity<Object> getPostByDate (@PathParam("date") LocalDateTime Date){
    //todo
//        return null;
//    }

//    @GetMapping("/byTag")
//    public ResponseEntity<Object> getPostsByTag (@PathParam("tag") String tag){
    //todo
//        return null;
//    }

//    @GetMapping("/moderation")
//    public ResponseEntity<Object> getPostForModeration (){
    //todo
//        return null;
//    }

    /**
     * выводит посты, которые создал текущий пользователь(соотвествующий id)
     *
     * @return 200 Status при любом ответе
     * Посты воращаются в формате SortedPostsResponse, при отсутствии постов вернуть пустой класс
     */
    @GetMapping("/my")
    public ResponseEntity<SortedPostsResponse> getUserPosts(HttpServletRequest request,
                                                            @RequestParam String status,
                                                            @RequestParam int offset,
                                                            @RequestParam int limit) {
        String sessionId = request.getRequestedSessionId();
        int id = userService.findAuthUser(sessionId).getId();

        SortedPostsResponse sortedPostsResponse = postService.getSortedPostsById(id, status, offset, limit);

        return new ResponseEntity<>(sortedPostsResponse, HttpStatus.OK);
    }

    /**
     * Редактирование поста
     * PUT запрос /api/post/{id}
     *
     * @param id - номер редактируемого поста
     * @return resultError
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResultErrorResponse> editPost(HttpServletRequest request, @PathVariable("id") int id, @RequestBody NewPostRequest newPostRequest) {
        //todo
        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

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
     *
     * @param newVoteRequest - номер поста
     * @return result   = true - если лайк прошел,
     * = false - если нет
     */
    @PostMapping("/like")
    public ResponseEntity<ResultErrorResponse> postLike(HttpServletRequest request, @RequestBody NewVoteRequest newVoteRequest) {

        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);
        Post post = postService.findById(newVoteRequest.getPostId());

        ResultErrorResponse resultErrorResponse = postVoteService.addLike(post, user);

        return new ResponseEntity<>(resultErrorResponse, HttpStatus.OK);
    }

    //добавление дизлайка
    @PostMapping("/dislike")
    public ResponseEntity<ResultErrorResponse> postDislike(HttpServletRequest request, @RequestBody NewVoteRequest newVoteRequest) {

        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);
        Post post = postService.findById(newVoteRequest.getPostId());

        ResultErrorResponse resultErrorResponse = postVoteService.addDislike(post, user);

        return new ResponseEntity<>(resultErrorResponse, HttpStatus.OK);
    }
}
