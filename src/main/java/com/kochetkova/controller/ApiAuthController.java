package com.kochetkova.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.web.JsonPath;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;

@Controller
@RequestMapping("/api/auth")
public class ApiAuthController {

//ВХОД
    @PostMapping("/login")
    public ResponseEntity<Object> getPostsToYear() {
        //todo
        return null;
    }

    //статус авторизации
    @GetMapping("/check")
    public ResponseEntity<Object> checkAuthStatus() {
        //todo
        return null;
    }

    //ВОсстановление пароля
    @PostMapping("/restore")
    public ResponseEntity<Object> restorePassword() {
        //todo
        return null;
    }

    //Изменение пароля
    @PostMapping("/password")
    public ResponseEntity<Object> setNewPassword() {
        //todo
        return null;
    }

    //Регистрация
    @PostMapping("/register")
    public ResponseEntity<Object> registerNewUser() {

        //todo
        return null;
    }

    //Запрос капчи
    @GetMapping("/captcha")
    public ResponseEntity<Object> getCaptcha() {
        //todo
        return null;
    }

    //Выход пользователя
    @GetMapping("/logout")
    public ResponseEntity<Object> logoutUser() {
        //todo
        return null;
    }

}
