package com.kochetkova.controller;

import com.kochetkova.api.response.BlogInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ApiGeneralController {

    @GetMapping("/init")
    public ResponseEntity<Object> getDescription(){
        BlogInfo blogDescription = new BlogInfo();

        return new ResponseEntity<>(blogDescription, HttpStatus.OK);
    }


}
