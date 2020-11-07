package com.kochetkova.controller;

import com.kochetkova.service.impl.PostServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(PostServiceImpl.PostNotFoundException.class)
    public ResponseEntity<messageException> postNotFoundException()  {

        return new ResponseEntity<>(new messageException("Post not found in DB"), HttpStatus.NOT_FOUND);
    }

    @Data
    @AllArgsConstructor
    private class messageException {
       private String message;
    }
}
