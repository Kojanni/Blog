package com.kochetkova.controller;

import com.kochetkova.api.request.NewPostRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.api.response.SortedPostsResponse;
import com.kochetkova.model.ModerationStatus;
import com.kochetkova.model.Post;
import com.kochetkova.model.User;
import com.kochetkova.service.PostService;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Transactional
@RequestMapping("/api/post")
public class ApiPostController {
    private UserService userService;
    private PostService postService;

    @Autowired
    public ApiPostController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
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
//пост по поисковому запросу
//    @GetMapping("/search")
    //todo
//    public ResponseEntity<Object> getPostsWithQuery (@PathParam("query") String query){
    //todo
//return null;
//    }

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

        if (post.getUser().getId() != user.getId() || user.getIsModerator() != 1) {
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


//    @PutMapping("/{id}")
//    public ResponseEntity<Object> editPost (@PathVariable("id") int id){
    //todo
//        return null;
//    }

    //добавление лайка
    @PostMapping("/like")
    public ResponseEntity<Object> postLike() {
        //todo
        return null;
    }

    //добавление дизлайка
    @PostMapping("/dislike")
    public ResponseEntity<Object> postDislike() {
        //todo
        return null;
    }
}
