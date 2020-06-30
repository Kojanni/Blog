package com.kochetkova.controller;

import com.kochetkova.api.response.BlogInfo;
import com.kochetkova.api.response.SortedPosts;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/post")
public class ApiPostController {

    @GetMapping("/")
    public ResponseEntity<Object> getPostsList (){
        SortedPosts sortedPosts = new SortedPosts();

        return new ResponseEntity<>(sortedPosts, HttpStatus.OK);
    }
}
