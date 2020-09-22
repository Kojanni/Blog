package com.kochetkova.controller;

import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.model.User;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class FileUploadExceptionAdvice {
    private UserService userService;

    @Autowired
    public FileUploadExceptionAdvice(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResultErrorResponse> handleMaxSizeException(HttpServletRequest request)  {
        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

        ResultErrorResponse resultError = new ResultErrorResponse();
        ErrorResponse error = userService.checkEditProfile(user, user.getName(), user.getEmail(), user.getPassword(), null);
        resultError.setErrors(error);

        return new ResponseEntity<>(resultError, HttpStatus.BAD_REQUEST);
    }

}
