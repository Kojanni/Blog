package com.kochetkova.controller;

import com.kochetkova.api.request.Login;
import com.kochetkova.api.request.NewUser;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.*;
import com.kochetkova.model.User;
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
    public ResponseEntity<AuthUserResponse> login(HttpServletRequest request, @RequestBody Login login) {
        AuthUserResponse authUser = new AuthUserResponse();
        User userInfo = userService.findUserByEmail(login.getEmail());

        if (!(userInfo == null || !userInfo.getPassword().equals(login.getPassword()))) {
            UserResponse user = getUserResponse(userInfo);

            String sessionId = request.getRequestedSessionId();
            userService.saveSession(sessionId, userInfo);

            authUser.setResult(true);
            authUser.setUserResponse(user);
        }
        return new ResponseEntity<>(authUser, HttpStatus.OK);
    }

    private UserResponse getUserResponse(User userInfo) {
        UserResponse.UserResponseBuilder userResponseBuilder = UserResponse.builder();
        userResponseBuilder.id(userInfo.getId());
        userResponseBuilder.name(userInfo.getName());
        userResponseBuilder.photo(userInfo.getPhoto());
        userResponseBuilder.email(userInfo.getEmail());
        if (userInfo.getIsModerator() == 1) {
            userResponseBuilder.moderation(true);
            userResponseBuilder.setting(true);
        }
        userResponseBuilder.moderationCount(userInfo.getModerationPosts().size());
        return userResponseBuilder.build();
    }

    //статус авторизации
    @GetMapping("/check")
    public ResponseEntity<AuthUserResponse> checkAuthStatus(HttpServletRequest request) {
        AuthUserResponse authUser = new AuthUserResponse();

        String sessionId = request.getRequestedSessionId();
        if (userService.findAuthSession(sessionId)) {
            User userInfo = userService.findAuthUser(sessionId);
            UserResponse user = getUserResponse(userInfo);

            authUser.setResult(true);
            authUser.setUserResponse(user);
        }

        return new ResponseEntity<>(authUser, HttpStatus.OK);
    }

    //Восстановление пароля
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

    //Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<ResultErrorResponse> register(@RequestBody NewUser newUser) {
        ResultErrorResponse result = new ResultErrorResponse();
        result.setResult(true);
        ErrorResponse.ErrorResponseBuilder errorBuilder = ErrorResponse.builder();
        if (!captchaCodeService.checkCaptcha(newUser.getCaptcha(), newUser.getCaptchaSecret())) {
            errorBuilder.captcha("Код с картинки введен неверно");
            result.setResult(false);
        }
        if (userService.isPresentUserByEmail(newUser.getEmail())) {
            errorBuilder.email("Этот e-mail уже зарегистрирован");
            result.setResult(false);
        }
        if (!userService.checkEmail(newUser.getEmail())) {
            errorBuilder.email("Некорректный e-mail");
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
            if(!userService.addNewUser(newUser)) {
                result.setResult(false);
            }
        }
        result.setErrors(errorBuilder.build());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //Запрос капчи
    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() throws IOException {
        return new ResponseEntity<>(captchaCodeService.getCaptcha(), HttpStatus.OK);
    }

    //Выход пользователя
    @GetMapping("/logout")
    public ResponseEntity<ResultErrorResponse> logoutUser(HttpServletRequest request) {
        String sessionId = request.getRequestedSessionId();
        if (userService.findAuthSession(sessionId)) {
            userService.deleteSession(sessionId);
        }
        ResultErrorResponse result = new ResultErrorResponse();
        result.setResult(true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
