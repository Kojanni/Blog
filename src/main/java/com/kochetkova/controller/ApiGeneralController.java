package com.kochetkova.controller;

import com.kochetkova.api.request.AcceptedPost;
import com.kochetkova.api.request.EditProfile;
import com.kochetkova.api.response.BlogInfo;
import com.kochetkova.api.response.Error;
import com.kochetkova.api.response.ResultError;
import com.kochetkova.api.response.Settings;
import com.kochetkova.api.response.TagWeight;
import com.kochetkova.model.GlobalSetting;
import com.kochetkova.model.User;
import com.kochetkova.repository.SettingsRepository;
import com.kochetkova.service.SettingsService;
import com.kochetkova.service.UserService;
import com.kochetkova.service.impl.SettingsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
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
        TagWeight tagWeight = new TagWeight();
        return new ResponseEntity<>(tagWeight, HttpStatus.OK);
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
    @PostMapping("/profile/my")
    public ResponseEntity<ResultError> editProfile(HttpServletRequest request, @RequestBody EditProfile editProfile) {
        //todo
        String sessionId = request.getRequestedSessionId();
        ResultError resultError = new ResultError();
        resultError.setResult(true);
        Error.ErrorBuilder errorBuilder = Error.builder();

        User user = userService.findAuthUser(sessionId);

        if (userService.checkName(editProfile.getName())) {
            if (!user.getName().contains(editProfile.getName())) {
                user.setName(editProfile.getName());
            }
        } else {
            errorBuilder.name("Имя указано неверно");
            resultError.setResult(false);
        }

        if (!user.getEmail().contains(editProfile.getEmail())) {
                if (userService.findUserByEmail(editProfile.getEmail()) == null) {
                    user.setEmail(editProfile.getEmail());
                } else {
                    errorBuilder.email("Этот e-mail уже зарегистрирован");
                    resultError.setResult(false);
                }
            }


        if (editProfile.getPassword() != null) {
            if (userService.checkPassword(editProfile.getPassword())) {
                user.setPassword(editProfile.getPassword());
            } else {
                errorBuilder.password("Пароль короче 6 символов");
                resultError.setResult(false);
            }
        }

        if (editProfile.getRemovePhoto() == 1) {
           /* if (editProfile.getPhoto().contains("")) {
                //todo
                //проверка фото, изменение размера
                System.out.println("УДАЛИТЬ ФОТО");
            } else {
                System.out.println("ИЗМЕНИТЬ РАЗМЕР 36х36");
            }*/
        }
        if (resultError.isResult()) {
            userService.saveUser(user);
        }
        resultError.setErrors(errorBuilder.build());
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
