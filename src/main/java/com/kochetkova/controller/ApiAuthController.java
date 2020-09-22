package com.kochetkova.controller;

import com.kochetkova.api.request.LoginRequest;
import com.kochetkova.api.request.NewUserRequest;
import com.kochetkova.api.request.ResetPasswordRequest;
import com.kochetkova.api.request.UserEmailRequest;
import com.kochetkova.api.response.*;
import com.kochetkova.model.User;
import com.kochetkova.service.CaptchaCodeService;
import com.kochetkova.service.SettingsService;
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
    private SettingsService settingsService;


    @Autowired
    public ApiAuthController(CaptchaCodeService captchaCodeService, UserService userService, SettingsService settingsService) {
        this.captchaCodeService = captchaCodeService;
        this.userService = userService;
        this.settingsService = settingsService;
    }

    //ВХОД
    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(HttpServletRequest request, @RequestBody LoginRequest login) {
        AuthUserResponse authUser = new AuthUserResponse();
        User userInfo = userService.findUserByEmail(login.getEmail());

        if (!(userInfo == null || !userInfo.getPassword().equals(login.getPassword()))) {
            UserResponse user = userService.createUserResponse(userInfo);

            String sessionId = request.getRequestedSessionId();
            userService.saveSession(sessionId, userInfo);

            authUser.setResult(true);
            authUser.setUserResponse(user);
        }
        return new ResponseEntity<>(authUser, HttpStatus.OK);
    }

    //статус авторизации
    @GetMapping("/check")
    public ResponseEntity<AuthUserResponse> checkAuthStatus(HttpServletRequest request) {
        AuthUserResponse authUser = new AuthUserResponse();

        String sessionId = request.getRequestedSessionId();
        if (userService.findAuthSession(sessionId)) {
            User user = userService.findAuthUser(sessionId);
            UserResponse userResponse = userService.createUserResponse(user);

            authUser.setResult(true);
            authUser.setUserResponse(userResponse);
        }

        return new ResponseEntity<>(authUser, HttpStatus.OK);
    }

    /**
     * Восстановление пароля
     * POST /api/auth/restore
     * Авторизация: не требуется
     * Если пользователь найден, ему должно отправляться письмо со ссылкой на восстановление пароля следующего вида - /login/change-password/HASH.
     * @param userEmail - e-mail пользователя
     * @return ResultErrorResponse "result": true или false
     */
    @PostMapping("/restore")
    public ResponseEntity<ResultErrorResponse> restorePassword(@RequestBody UserEmailRequest userEmail) {
        //todo: восстановление пароля, почта
        ResultErrorResponse result = userService.restorePassword(userEmail.getEmail());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Изменение пароля
     * POST /api/auth/password
     * Авторизация: не требуется
     * @param resetPasswordRequest - код восстановления пароля и коды капчи
     * @return ResultErrorResponse
     * В случае, если все данные отправлены верно: "result": true
     * В случае ошибок: "result": false + "errors":
     */
    @PostMapping("/password")
    public ResponseEntity<ResultErrorResponse> setNewPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        //todo
        ResultErrorResponse resultErrorResponse = userService.setNewPassword(resetPasswordRequest);

        return  new ResponseEntity<>(resultErrorResponse, HttpStatus.OK);
    }

    //Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<ResultErrorResponse> register(@RequestBody NewUserRequest newUser) {
        //проверка глобальных настроек:
        if (!settingsService.getSettings().isMultiuserMode()) {
            new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        //--
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
            if (!userService.addNewUser(newUser)) {
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
