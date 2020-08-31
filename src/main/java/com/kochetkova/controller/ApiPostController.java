package com.kochetkova.controller;

import com.kochetkova.api.request.AddedPost;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.PostResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.api.response.SortedPostsResponse;
import com.kochetkova.model.Post;
import com.kochetkova.model.User;
import com.kochetkova.service.PostService;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/post")
public class ApiPostController {
    private UserService userService;
    private PostService postService;

    @Autowired
    public ApiPostController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    //добавляет пост
    @PostMapping("")
    public ResponseEntity<ResultErrorResponse> addPosts(HttpServletRequest request, @RequestBody AddedPost addedPost) {
//    todo
        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

        ResultErrorResponse resultError = new ResultErrorResponse();
        ErrorResponse error = postService.checkAddedPost(addedPost);
        if (!error.isPresent()) {
            resultError.setResult(true);
            postService.addPost(addedPost, user);
        } else {
            resultError.setErrors(error);
        }
        return new ResponseEntity<>(resultError, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<SortedPostsResponse> getPostsList(@RequestParam String mode,
                                                            @RequestParam int offset,
                                                            @RequestParam int limit) {
        SortedPostsResponse sortedPosts = new SortedPostsResponse();
        List<PostResponse> posts = postService.getSortedPosts(mode);
        sortedPosts.setCount(posts.size());
        sortedPosts.setPosts(posts);

        return new ResponseEntity<>(sortedPosts, HttpStatus.OK);
    }
//пост по поисковому запросу
//    @GetMapping("/search")
    //todo
//    public ResponseEntity<Object> getPostsWithQuery (@PathParam("query") String query){
    //todo
//return null;
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Object> getPost (@PathVariable int ID){
    //todo
//        return null;
//    }

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

//выводит посты, которые создал я (пользователь)
//    @GetMapping("/my")
//    public ResponseEntity<Object> getMyPosts (int id){
    //todo
//        return null;
//    }


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
