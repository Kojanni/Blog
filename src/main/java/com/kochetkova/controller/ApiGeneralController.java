package com.kochetkova.controller;

import com.kochetkova.api.request.AcceptPostRequest;
import com.kochetkova.api.request.EditProfileRequest;
import com.kochetkova.api.response.*;
import com.kochetkova.api.response.BlogInfoResponse;
import com.kochetkova.api.response.ErrorResponse;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@ControllerAdvice
@RequestMapping("/api")
public class ApiGeneralController {
    private final BlogInfoResponse blogInfoResponse;
    private SettingsService settingsService;
    private UserService userService;

    @Autowired
    public ApiGeneralController(SettingsService settingsService, UserService userService, BlogInfoResponse blogInfoResponse) {
        this.settingsService = settingsService;
        this.userService = userService;
        this.blogInfoResponse = blogInfoResponse;
    }

    @GetMapping("/init")
    public ResponseEntity<BlogInfoResponse> getDescription() {
        return new ResponseEntity<>(blogInfoResponse, HttpStatus.OK);
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
                                                       @RequestBody AcceptPostRequest acceptPostRequest) {
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
    public ResponseEntity<ResultErrorResponse> editProfileWithPhoto(HttpServletRequest request,
                                                                    @RequestParam MultipartFile photo,
                                                                    @RequestParam String name,
                                                                    @RequestParam String email,
                                                                    @RequestParam(required = false) String password,
                                                                    @RequestParam Integer removePhoto) {

        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

        ErrorResponse error = userService.checkEditProfile(user, name, email, password, photo);

        ResultErrorResponse resultError = new ResultErrorResponse();

        if (error.isPresent()) {
            resultError.setErrors(error);
        } else {
            resultError.setResult(true);
            userService.saveEditProfile(user, name, email, password, photo, removePhoto);
        }
        return new ResponseEntity<>(resultError, HttpStatus.OK);
    }


    @PostMapping("/profile/my")
    public ResponseEntity<ResultErrorResponse> editProfile(HttpServletRequest request, @RequestBody EditProfileRequest editProfile) {
        //todo
        String sessionId = request.getRequestedSessionId();
        User user = userService.findAuthUser(sessionId);

        ErrorResponse error = userService.checkEditProfile(user, editProfile);

        ResultErrorResponse resultError = new ResultErrorResponse();
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
    public ResponseEntity<SettingsResponse> getSettings() {
        List<GlobalSetting> globalSettings = settingsService.getAll();
        SettingsResponse settings = new SettingsResponse();
        settings.getSettings(globalSettings);
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }
}
