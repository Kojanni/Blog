package com.kochetkova.controller;

import com.kochetkova.api.response.AuthUserResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.service.impl.PostServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(PostServiceImpl.PostNotFoundException.class)
    public ResponseEntity<ResultErrorResponse> postNotFoundException() {

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AuthUserResponse> findAuthenticationException() {

        return new ResponseEntity<>(new AuthUserResponse(), HttpStatus.OK);
    }
}
