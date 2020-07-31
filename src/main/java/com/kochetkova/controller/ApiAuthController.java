package com.kochetkova.controller;

import com.kochetkova.api.request.NewUser;
import com.kochetkova.api.response.AuthUser;
import com.kochetkova.api.response.Captcha;
import com.kochetkova.api.response.Error;
import com.kochetkova.api.response.ResultError;
import com.kochetkova.api.response.User;
import com.kochetkova.service.CaptchaCodeService;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/api/auth")
public class ApiAuthController {
    private CaptchaCodeService captchaCodeService;
    private UserService userService;

    @Autowired
    public ApiAuthController(CaptchaCodeService captchaCodeService, UserService userService) {
        this.captchaCodeService = captchaCodeService;
        this.userService = userService;
    }

    //ВХОД
    @PostMapping("/login")
    public ResponseEntity<Object> login() {
        //todo

        return null;
    }

    //статус авторизации
    @GetMapping("/check")
    public ResponseEntity<AuthUser> checkAuthStatus() {
        AuthUser authUser = new AuthUser();
        User.UserBuilder userBuilder = User.builder();
        userBuilder.id(576);
        userBuilder.name("Дмитрий Петров");
        userBuilder.photo("/avatars/ab/cd/ef/52461.jpg");
        userBuilder.email("petrov@petroff.ru");
        userBuilder.moderation(true);
        userBuilder.moderationCount(56);
        userBuilder.setting(true);
        //authUser.setResult(true);
        //authUser.setUser(userBuilder.build());
        //todo
        return new ResponseEntity<>(authUser, HttpStatus.OK);
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
    public ResponseEntity<ResultError> register(@RequestBody NewUser newUser) {
        ResultError result = new ResultError();
        result.setResult(true);
        Error.ErrorBuilder errorBuilder = Error.builder();
        if (!captchaCodeService.checkCaptcha(newUser.getCaptcha(), newUser.getCaptchaSecret())) {
            errorBuilder.captcha("Код с картинки введен неверно");
            result.setResult(false);
        }
        if (userService.isPresent(newUser)) {
            errorBuilder.password("Этот e-mail уже зарегистрирован");
            result.setResult(false);
        }
        if (!userService.checkEmail(newUser.getEmail())) {
            errorBuilder.password("Некорректный e-mail");
            result.setResult(false);
        }
        if (!userService.checkName(newUser.getName())) {
            errorBuilder.name("Имя указано неверно");
            result.setResult(false);
        }
        if (!userService.checkPassword(newUser.getPassword())) {
            errorBuilder.password("Некорректный пароль");
            result.setResult(false);
        }
        if (result.isResult()) {
            userService.addNewUser(newUser);
        }
        result.setErrors(errorBuilder.build());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //Запрос капчи
    @GetMapping("/captcha")
    public ResponseEntity<Captcha> getCaptcha() throws IOException {
        return new ResponseEntity<>(captchaCodeService.getCaptcha(), HttpStatus.OK);
    }

    //Выход пользователя
    @GetMapping("/logout")
    public ResponseEntity<Object> logoutUser() {
        //todo
        return null;
    }

}
