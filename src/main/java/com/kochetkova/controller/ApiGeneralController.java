package com.kochetkova.controller;

import com.kochetkova.api.request.AcceptedPost;
import com.kochetkova.api.request.EditProfile;
import com.kochetkova.api.response.BlogInfo;
import com.kochetkova.api.response.Error;
import com.kochetkova.api.response.ResultError;
import com.kochetkova.api.response.Settings;
import com.kochetkova.api.response.TagWeightResponse;
import com.kochetkova.model.GlobalSetting;
import com.kochetkova.model.User;
import com.kochetkova.service.SettingsService;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@ControllerAdvice
@RequestMapping("/api")
public class ApiGeneralController {
    private final BlogInfo blogInfo;
    private SettingsService settingsService;
    private UserService userService;

    @Autowired
    public ApiGeneralController(SettingsService settingsService, UserService userService, BlogInfo blogInfo) {
        this.settingsService = settingsService;
        this.userService = userService;
        this.blogInfo = blogInfo;
    }

    @GetMapping("/init")
    public ResponseEntity<BlogInfo> getDescription() {
        return new ResponseEntity<>(blogInfo, HttpStatus.OK);
    }

    //загружает картинку на сервер, возвращает путь до изображения
//    @PostMapping("/image")
//    public String loadPicForPost (){
    //todo
//        return null;
//    }


//    @PostMapping("/comment/")
//    public ResponseEntity<Object> addCommentToPost (@PathParam("parent_id") int parentId, @PathParam("post_id") int postId, @PathParam("text") String text){
//        todo
    //json-obj for response
//        return null;
//    }


    @GetMapping("/tag")
    public ResponseEntity<Object> getTagByQuery(@RequestParam(value = "query", required = false) String query) {
        //todo
        TagWeightResponse tagWeightResponse = new TagWeightResponse();
        return new ResponseEntity<>(tagWeightResponse, HttpStatus.OK);
    }


    @PostMapping("/moderation")
    public ResponseEntity<Object> postModerationStatus(@RequestParam(value = "query", required = false) String query,
                                                       @RequestBody AcceptedPost acceptedPost) {
        //todo
        return null;
    }

////    @GetMapping("/calendar/")
//    public ResponseEntity<Object> getPostsToYear(@PathParam("year") int year) {
//        //todo
//        return null;
//    }

    //Редактирование профиля
    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultError> editProfileWithPhoto(HttpServletRequest request,
                                                            @RequestParam MultipartFile photo,
                                                            @RequestParam String name,
                                                            @RequestParam String email,
                                                            @RequestParam(required = false) String password,
                                                            @RequestParam Integer removePhoto) {

        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

        Error error = userService.checkEditProfile(user, name, email, password, photo);

        ResultError resultError = new ResultError();

        if (error.isPresent()) {
            resultError.setErrors(error);
        } else {
            resultError.setResult(true);
            userService.saveEditProfile(user, name, email, password, photo, removePhoto);
        }
        return new ResponseEntity<>(resultError, HttpStatus.OK);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResultError> handleMaxSizeException(HttpServletRequest request)  {
        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

        ResultError resultError = new ResultError();
        Error error = userService.checkEditProfile(user, user.getName(), user.getEmail(), user.getPassword(), null);
        resultError.setErrors(error);

        return new ResponseEntity<>(resultError, HttpStatus.OK);
    }

    @PostMapping("/profile/my")
    public ResponseEntity<ResultError> editProfile(HttpServletRequest request, @RequestBody EditProfile editProfile) {
        //todo
        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

        Error error = userService.checkEditProfile(user, editProfile);

        ResultError resultError = new ResultError();
        if (!error.isPresent()) {
            resultError.setResult(true);
            userService.saveEditProfile(user, editProfile);
        } else {
            resultError.setErrors(error);
        }

        return new ResponseEntity<>(resultError, HttpStatus.OK);
    }

    //СТатистика для текущего пользователя
    @GetMapping("/statistics/my")
    public ResponseEntity<Object> getUserStatistics() {
        //todo
        return null;
    }

    //СТатистика блога
    @GetMapping("/statistics/all")
    public ResponseEntity<Object> getStatistics() {
        //todo
        return null;
    }

    //Получение настроек
    @GetMapping("/settings")
    public ResponseEntity<Settings> getSettings() {
        List<GlobalSetting> globalSettings = settingsService.getAll();
        Settings settings = new Settings();
        settings.getSettings(globalSettings);
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }
}
