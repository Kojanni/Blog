package com.kochetkova.controller;

import com.kochetkova.api.request.ModerationPostRequest;
import com.kochetkova.api.request.EditProfileRequest;
import com.kochetkova.api.request.NewCommentRequest;
import com.kochetkova.api.request.SettingsRequest;
import com.kochetkova.api.response.*;
import com.kochetkova.api.response.BlogInfoResponse;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.model.User;
import com.kochetkova.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
public class ApiGeneralController {
    private final BlogInfoResponse blogInfoResponse;
    private SettingsService settingsService;
    private UserService userService;
    private TagService tagService;
    private PostService postService;
    private PostCommentService postCommentService;

    @Autowired
    public ApiGeneralController(SettingsService settingsService, UserService userService, BlogInfoResponse blogInfoResponse, TagService tagService, PostService postService, PostCommentService postCommentService) {
        this.settingsService = settingsService;
        this.userService = userService;
        this.blogInfoResponse = blogInfoResponse;
        this.tagService = tagService;
        this.postService = postService;
        this.postCommentService = postCommentService;
    }

    /**
     * Общие данные блога
     * GET /api/init/
     * название блога
     * подзаголовок для размещения в хэдере сайта,
     * номер телефона,
     * e-mail и
     * информацию об авторских правах для размещения в футере.
     *
     * @return BlogInfoResponse
     */
    @GetMapping("/init")
    public ResponseEntity<BlogInfoResponse> getDescription() {
        return new ResponseEntity<>(blogInfoResponse, HttpStatus.OK);
    }


    /**
     * Загрузка изображений
     * POST /api/image
     * Авторизация: требуется
     * Запрос: Content-Type: multipart/form-data
     *
     * @return Метод возвращает путь до изображения
     */
    @PostMapping("/image")
    public ResponseEntity<Object> loadPicForPost(HttpServletRequest request,
                                                 @RequestParam MultipartFile image) {
        //Авторизация есть?
        User user = userService.findAuthUser(request.getRequestedSessionId());
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        //--

        //todo Загрузка изображений
        String imagePath = postService.savePostImage(image);
        return new ResponseEntity<>(imagePath, HttpStatus.OK);
    }

    /**
     * Отправка комментария к посту
     * POST запрос /api/comment
     * Метод добавляет комментарий к посту.
     *
     * @param newCommentRequest - данные для нового комментария
     * @return Если параметры parent_id и/или post_id неверные (соответствующие комментарий и/или пост не существуют),
     * должна выдаваться ошибка 400 (см. ниже раздел “Обработка ошибок”).
     * В случае, если текст комментария отсутствует (пустой) или слишком короткий, необходимо выдавать ошибку в JSON-формате.
     */
    @PostMapping("/comment")
    public ResponseEntity<Object> addCommentToPost(HttpServletRequest request,
                                                   @RequestBody NewCommentRequest newCommentRequest) {

        //Авторизация есть?
        User user = userService.findAuthUser(request.getRequestedSessionId());
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        //--

        ResultErrorResponse resultError = postCommentService.checkNewCommentRequestData(newCommentRequest);
        if (!resultError.isResult()) {
            if (resultError.getErrors().getBadRequest() != null) { //400
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (resultError.getErrors().isPresent()) { //error
                return new ResponseEntity<>(resultError, HttpStatus.OK);
            }
        }

        AddedCommentIdResponse commentIdResponse = postCommentService.addNewComment(newCommentRequest, user);

        return new ResponseEntity<>(commentIdResponse, HttpStatus.OK);
    }

    /**
     * Получение списка тэгов
     * GET /api/tag/
     * Метод выдаёт список тэгов, начинающихся на строку, заданную в параметре
     * В случае, если она не задана, выводятся все тэги.
     *
     * @param query -  часть тэга или тэг, может быть не задан или быть пустым.
     * @return TagWeightResponse - список тегов с относительным нормированным весом
     */
    @GetMapping("/tag")
    public ResponseEntity<TagWeightResponse> getTag(@RequestParam(value = "query", required = false) String query) {
        TagWeightResponse tagWeightResponse = tagService.getTagWeightResponse(query);

        return new ResponseEntity<>(tagWeightResponse, HttpStatus.OK);
    }

    /**
     * Модерация поста
     * POST запрос /api/moderation
     *
     * @param moderationPostRequest - данные для изменения статуса поста
     * @return ResultErrorResponse:
     * result = true - если все изменено
     * result = false - если по какой-то причине изменить статус не удалось
     */
    @PostMapping("/moderation")
    public ResponseEntity<ResultErrorResponse> postModerationStatus(HttpServletRequest request,
                                                                    @RequestBody ModerationPostRequest moderationPostRequest) {
        //todo
        //Авторизация есть?
        User user = userService.findAuthUser(request.getRequestedSessionId());
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        //--

        ResultErrorResponse result = new ResultErrorResponse();

        if (user.getIsModerator() == 0) {
            result.setResult(false);
        } else {
            result.setResult(postService.changeModerationStatus(moderationPostRequest));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Календарь(количество публикаций)
     * GET  запрос /api/calendar
     *
     * @param year - год, за который необходимо подсчитать количество,
     *             если не задан, то за все года
     * @return CalendarResponse - год(года) и список дата-количество
     */
    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> getPostsToYear(@RequestParam(value = "year", required = false) Integer year) {

        CalendarResponse calendarResponse = postService.getPostsCountByYear(year);

        return new ResponseEntity<>(calendarResponse, HttpStatus.OK);
    }

    /**
     * Редактирование моего профиля. Запрос c изменением данных с фото
     * POST /api/profile/my
     */
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

    /**
     * Редактирование моего профиля. Запрос c изменением данных без фото
     * POST запрос /api/profile/my
     *
     * @param editProfile - данные для редактирования
     * @return ResultErrorResponse: результат true, если все изменено без ошибок
     * резутат false + список ошибок, если нет
     */
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

    /**
     * СТатистика для текущего пользователя
     * GET запрос /api/statistics/my
     * Авторизация: требуется
     *
     * @return StatisticsResponse - общие количества параметров для всех публикаций, у который он является автором и доступные для чтения
     */
    @GetMapping("/statistics/my")
    public ResponseEntity<StatisticsResponse> getUserStatistics(HttpServletRequest request) {
        //Авторизация есть?
        User user = userService.findAuthUser(request.getRequestedSessionId());
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        //--

        StatisticsResponse statisticsResponse = postService.getUserStatistics(user);

        return new ResponseEntity<>(statisticsResponse, HttpStatus.OK);
    }

    /**
     * Статистика по всему блогу
     * GET запрос /api/statistics/all
     * Авторизация: не требуется
     *
     * @return StatisticsResponse - общие количества параметров для всех публикаций, у который он является автором и доступные для чтения
     */
    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> getStatistics(HttpServletRequest request) {
        //проверка настроек:
        User user = userService.findAuthUser(request.getRequestedSessionId());
        boolean isPublic = settingsService.getSettings().isStatisticsIsPublic();

        if (isPublic || (!isPublic && user.getIsModerator() == (byte) 1)) {
            return new ResponseEntity<>(postService.getStatistics(), HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Получение настроек
     * GET запрос /api/settings/
     * Авторизация: не требуется
     *
     * @return SettingsResponse Метод возвращает глобальные настройки блога из таблицы global_settings.
     */
    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> getSettings() {
        SettingsResponse settings = settingsService.getSettings();

        return new ResponseEntity<>(settings, HttpStatus.OK);
    }


    /**
     * Сохранение настроек
     * PUT запрос /api/settings/
     * Авторизация: требуется
     *
     * @return Метод записывает глобальные настройки блога в таблицу global_settings, если запрашивающий пользователь авторизован и является модератором.
     */
    @PutMapping("/settings")
    public ResponseEntity<SettingsResponse> putSettings(HttpServletRequest request, @RequestBody SettingsRequest settingsRequest) {
        //Авторизация есть?
        User user = userService.findAuthUser(request.getRequestedSessionId());
        if (user == null || user.getIsModerator() == 0) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        //--

        settingsService.saveSettings(settingsRequest);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
