package com.kochetkova.controller;

import com.kochetkova.api.request.AcceptedPost;
import com.kochetkova.api.response.BlogInfo;
import com.kochetkova.api.response.Settings;
import com.kochetkova.api.response.TagWeight;
import com.kochetkova.model.GlobalSetting;
import com.kochetkova.repository.SettingsRepository;
import com.kochetkova.service.SettingsService;
import com.kochetkova.service.impl.SettingsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class ApiGeneralController {
    private final BlogInfo blogInfo;
    @Autowired
    private SettingsService settingsService;


    public ApiGeneralController(BlogInfo blogInfo) {
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

//    //Редактирование профиля
//    @RequestMapping(value = "/profile/my", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
//    public ResponseEntity<Object> restorePassword(/*@RequestBody тут должен быть интерфейс общий и разходжится на 3 класса?*/) {
//        //todo
//        return null;
//    }

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
