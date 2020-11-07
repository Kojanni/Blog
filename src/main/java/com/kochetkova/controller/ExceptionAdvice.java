package com.kochetkova.controller;

import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.service.impl.PostServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(PostServiceImpl.PostNotFoundException.class)
    public ResponseEntity<ResultErrorResponse> postNotFoundException()  {

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
