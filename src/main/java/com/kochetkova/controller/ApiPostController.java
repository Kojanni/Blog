package com.kochetkova.controller;

import com.kochetkova.api.response.BlogInfo;
import com.kochetkova.api.response.SortedPosts;
import com.kochetkova.model.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/api/post")
public class ApiPostController {

    @GetMapping("")
    public ResponseEntity<Object> getPostsList (){
        SortedPosts sortedPosts = new SortedPosts();

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

//    @PostMapping("/")
//    public ResponseEntity<Object> getPostsWithQuery (Post post){
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
