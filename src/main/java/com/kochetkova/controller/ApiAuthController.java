package com.kochetkova.controller;

import com.kochetkova.api.request.Login;
import com.kochetkova.api.request.NewUser;
import com.kochetkova.api.response.Error;
import com.kochetkova.api.response.*;
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

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<AuthUser> login(HttpServletRequest request, @RequestBody Login login) {
        AuthUser authUser = new AuthUser();
        com.kochetkova.model.User userInfo = userService.findUserByEmail(login.getEmail());

        if (!(userInfo == null || !userInfo.getPassword().contains(login.getPassword()))) {
            User.UserBuilder userBuilder = User.builder();
            userBuilder.id(userInfo.getId());
            userBuilder.name(userInfo.getName());
            userBuilder.photo(userInfo.getPhoto());
            userBuilder.email(userInfo.getEmail());
            if (userInfo.getIsModerator() == 1) {
                userBuilder.moderation(true);
                userBuilder.setting(true);
            }
            userBuilder.moderationCount(userInfo.getModerationPosts().size());

            String sessionId = request.getRequestedSessionId();
            userService.saveSession(sessionId, userInfo.getId());

            authUser.setResult(true);
            authUser.setUser(userBuilder.build());
        }
        return new ResponseEntity<>(authUser, HttpStatus.OK);
    }

    //статус авторизации
    @GetMapping("/check")
    public ResponseEntity<AuthUser> checkAuthStatus(HttpServletRequest request) {
        AuthUser authUser = new AuthUser();
        User.UserBuilder userBuilder = User.builder();
        String sessionId = request.getRequestedSessionId();
        if (userService.findAuthSession(sessionId)) {
            com.kochetkova.model.User userInfo = userService.findAuthUser(sessionId);
            userBuilder.id(userInfo.getId());
            userBuilder.name(userInfo.getName());
            userBuilder.photo(userInfo.getPhoto());
            userBuilder.email(userInfo.getEmail());
            if (userInfo.getIsModerator() == 1) {
                userBuilder.moderation(true);
                userBuilder.setting(true);
            }
            userBuilder.moderationCount(userInfo.getModerationPosts().size());
            authUser.setResult(true);
            authUser.setUser(userBuilder.build());
        }
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
        if (userService.isPresentUserByEmail(newUser.getEmail())) {
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
            errorBuilder.password("Пароль короче 6-ти символов");
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
