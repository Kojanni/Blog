package com.kochetkova.service.impl;

import com.kochetkova.api.request.EditProfileRequest;
import com.kochetkova.api.request.NewUserRequest;
import com.kochetkova.api.request.ResetPasswordRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.api.response.UserResponse;
import com.kochetkova.model.ModerationStatus;
import com.kochetkova.model.User;
import com.kochetkova.repository.UserRepository;
import com.kochetkova.service.CaptchaCodeService;
import com.kochetkova.service.MailSender;
import com.kochetkova.service.UserService;
import com.kochetkova.service.impl.enums.ModeUserInfo;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CaptchaCodeService captchaCodeService;
    private final MailSender mailSender;

    private final AuthenticationManager authenticationManager;

    private static final String EMAIL_REG = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$";
    private static final String NAME_REG = "[A-ZА-Яa-zа-я]+";
    private final String separator = File.separator;

    private static Map<String, Integer> sessions = new HashMap<>();

    @Value("${photo.avatarPath}")
    private String photoPath;


    @Value("${photo.width}")
    private int photoWidth;


    @Value("${photo.height}")
    private int photoHeight;

    @Value("${password.length.min}")
    private int passwordLengthMin;

    private final PasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CaptchaCodeService captchaCodeService, MailSender mailSender, AuthenticationManager authenticationManager, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.captchaCodeService = captchaCodeService;
        this.mailSender = mailSender;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    @Override
    public User auth(String email, String password) {

        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User userAuth = (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        return findUserByEmail(userAuth.getUsername());
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    //Добавление нового юзера в БД
    @Override
    public boolean addNewUser(NewUserRequest newUser) {
        if (checkRegisteredUserData(newUser)) {
            User user = createNewUser(newUser);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    //Поиск пользовавтеля по email
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found in DB. "));
    }

    //Поиск пользовавтеля по ID
    @Override
    public User findUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    //Существует ли пользователь в БД с введеннолй почтой:
    @Override
    public boolean isPresentUserByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    //проверка корректности данных пользователя:
    @Override
    public boolean checkUserData(NewUserRequest user) {
        return checkEmail(user.getEmail())
                && checkPassword(user.getPassword())
                && checkName(user.getName());
    }

    //проверка корректности данных пользователя и его существование в БД для регистрации:
    @Override
    public boolean checkRegisteredUserData(NewUserRequest newUser) {
        return (checkUserData(newUser) && !isPresentUserByEmail(newUser.getEmail()));
    }

    //проверка длины пароля
    @Override
    public boolean checkPassword(String password) {
        return password.length() >= passwordLengthMin;
    }

    //проверка корректности имени пользователя
    @Override
    public boolean checkName(String name) {
        return name.matches(NAME_REG);
    }

    //проверка корректности почтового ящика пользователя
    @Override
    public boolean checkEmail(String email) {
        return email.matches(EMAIL_REG);
    }

    //ошибки в корректности данных пользователя для обновления профиля запрос с фото
    @Override
    public ErrorResponse checkEditProfile(User user, String name, String email, String password, MultipartFile photo) {
        ErrorResponse.ErrorResponseBuilder errorBuilder = ErrorResponse.builder();

        if (!user.getName().equals(name)) {
            if (!checkName(name)) {
                errorBuilder.name("Имя указано неверно");
            }
        }

        if (!user.getEmail().equalsIgnoreCase(email)) {
            if (findUserByEmail(email) != null) {
                errorBuilder.email("Этот e-mail уже зарегистрирован");
            }
        }

        if (password != null) {
            if (!checkPassword(password)) {
                errorBuilder.password("Пароль короче 6 символов");
            }
        }

        if (photo == null) {
            errorBuilder.photo("Фото слишком большое, нужно не более 5Мб");
        }

        return errorBuilder.build();
    }

    //ошибки в корректности данных пользователя для обновления профиля запрос без фото
    @Override
    public ErrorResponse checkEditProfile(User user, EditProfileRequest editProfile) {
        ErrorResponse.ErrorResponseBuilder errorBuilder = ErrorResponse.builder();

        if (!user.getName().equals(editProfile.getName())) {
            if (!checkName(editProfile.getName())) {
                errorBuilder.name("Имя указано неверно");
            }
        }

        if (!user.getEmail().equalsIgnoreCase(editProfile.getEmail())) {
            if (findUserByEmail(editProfile.getEmail()) != null) {
                errorBuilder.email("Этот e-mail уже зарегистрирован");
            }
        }

        if (editProfile.getPassword() != null) {
            if (!checkPassword(editProfile.getPassword())) {
                errorBuilder.password("Пароль короче 6 символов");
            }
        }

        return errorBuilder.build();
    }

    /**
     * сохранить данные пользователя для обновления профиля запрос с фото
     *
     * @param user        - пользователь
     * @param name        - имя
     * @param email       - почта
     * @param password    - пароль
     * @param photo       - фотография
     * @param removePhoto - удалить фото при 1, сменить фото - 0
     * @return User - пользователь с изменными данными
     */
    @Override
    public User saveEditProfile(User user, String name, String email, String password, MultipartFile photo, Integer removePhoto) {
        if (!user.getName().equals(name)) {
            user.setName(name);
        }

        if (!user.getEmail().equalsIgnoreCase(email)) {
            user.setEmail(email);
        }

        if (password != null) {
            user.setPassword(password);
        }


        if (removePhoto != null && removePhoto == 0) {
            //change photo
            user.setPhoto(savePhoto(user, photo));
        }
        if (removePhoto != null && removePhoto == 1) {
            //delete photo
            deletePhoto(user);
            user.setPhoto("");
        }

        saveUser(user);
        return user;
    }

    //сохранить данные пользователя для обновления профиля запрос без фото
    @Override
    public User saveEditProfile(User user, EditProfileRequest editProfile) {
        return saveEditProfile(user, editProfile.getName(), editProfile.getEmail(), editProfile.getPassword(), null, editProfile.getRemovePhoto());
    }

    @Override
    public void saveSession(String sessionId, User user) {
        sessions.put(sessionId, user.getId());
    }

    @Override
    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }

    @Override
    public boolean findAuthSession(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    @Override
    public User findAuthUser(String sessionId) {
        if (sessions.get(sessionId) == null) {
            return null;
        }
        return findUserById(sessions.get(sessionId));
    }

    /**
     * cохранение новой фотографии в папке и установка ссылки у пользователя
     *
     * @param user  - пользователь
     * @param photo - фотография
     * @return строка с путем к фотографии
     */
    @Override
    public String savePhoto(User user, MultipartFile photo) {
        deletePhoto(user);

        if (!photo.isEmpty()) {
            int id = user.getId();

            int numberOfFolder = id / 100; //разбито на папки по 100 пользователей
            String fullPath = photoPath + separator + numberOfFolder + separator + id + "." + FilenameUtils.getExtension(photo.getOriginalFilename());
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                BufferedImage resizePhoto = resizePhoto(photo);

                ImageIO.write(resizePhoto, FilenameUtils.getExtension(photo.getOriginalFilename()), file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "/" + fullPath.replace("\\", "/");
        }
        return null;
    }

    /**
     * Изменение размера фотографии под параметры (ширина и высота), заданные в настройках приложения
     *
     * @param photo - исходная фотография
     * @return BufferedImage - параметризированная фотография
     * @throws IOException
     */
    private BufferedImage resizePhoto(MultipartFile photo) throws IOException {
        BufferedImage image = ImageIO.read(photo.getInputStream());

        return Scalr.resize(image,
                Scalr.Method.QUALITY,
                Scalr.Mode.FIT_EXACT,
                photoWidth,
                photoHeight,
                Scalr.OP_ANTIALIAS);
    }

    /**
     * удаление фотографии из папки для аватара пользователя
     *
     * @param user - пользователь
     * @return удалено - true или нет - false
     */
    @Override
    public boolean deletePhoto(User user) {

        String fullPath = user.getPhoto();
        if (fullPath == null || fullPath.equalsIgnoreCase("")) {
            return true;
        }
        File file = new File(fullPath.substring(1).replace("/", "\\"));

        return file.delete();
    }

    /**
     * создание нового юзера по введенным данным в формате NewUser
     */
    @Override
    public User createNewUser(NewUserRequest newUser) {
        User user = new User();
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        user.setPassword(encoder.encode(newUser.getPassword()));
        user.setIsModerator((byte) 0);
        user.setRegTime(LocalDateTime.now());

        return user;
    }

    /**
     * формирование UserResponse на основе User
     *
     * @param user - информация, полученная из БД, о пользователе
     * @param mode - режим полноты выдачи информации:
     *             1 - только id и name;
     *             2 - id, name, photo
     * @return объект класса UserResponse
     */
    @Override
    public UserResponse createUserResponse(User user, ModeUserInfo mode) {
        UserResponse.UserResponseBuilder userResponseBuilder = UserResponse.builder();

        userResponseBuilder.id(user.getId());
        userResponseBuilder.name(user.getName());

        if (mode.equals(ModeUserInfo.ID_NAME_PHOTO)) {
            userResponseBuilder.photo(user.getPhoto());
        }

        return userResponseBuilder.build();
    }

    @Override
    public UserResponse createUserResponse(User userInfo) {
        UserResponse.UserResponseBuilder userResponseBuilder = UserResponse.builder();
        userResponseBuilder.id(userInfo.getId());
        userResponseBuilder.name(userInfo.getName());
        userResponseBuilder.photo(userInfo.getPhoto());
        userResponseBuilder.email(userInfo.getEmail());
        if (userInfo.getIsModerator() == 1) {
            userResponseBuilder.moderation(true);
            userResponseBuilder.setting(true);
        }
        userResponseBuilder.moderationCount((int) userInfo.getModerationPosts().stream()
                .filter(post -> post.getModerationStatus() == ModerationStatus.NEW && post.getIsActive() == 1).count());
        return userResponseBuilder.build();
    }

    /**
     * Восстановление пароля
     * POST /api/auth/restore
     * Авторизация: не требуется
     * Если пользователь найден, ему должно отправляться письмо со ссылкой на восстановление пароля следующего вида - /login/change-password/HASH.
     *
     * @param email - e-mail пользователя
     * @return ResultErrorResponse "result": true или false
     */
    @Override
    public ResultErrorResponse restorePassword(String email) {
        ResultErrorResponse result = new ResultErrorResponse();
        User user = findUserByEmail(email);
        if (user != null) {
            user.setCode(UUID.randomUUID().toString().replace("-", ""));
            saveUser(user);
            String message = String.format("Hello, dear %s!\n"
                            + "Вы отправили запрос на восстановление пароля?\n"
                            + "Кто-то (надеемся, что вы) попросил нас сбросить пароль для вашей учетной записи. Чтобы сделать это, щелкните по ссылке ниже:\n"
                            + "http://localhost:8080/login/change-password/%s\n"
                            + "Если вы не запрашивали сброс пароля, игнорируйте это сообщение!",
                    user.getName(), user.getCode());
            mailSender.send(user.getEmail(), "Восстановление пароля", message);

            result.setResult(true);
        }
        return result;
    }

    /**
     * Изменение пароля
     * POST /api/auth/password
     * Авторизация: не требуется
     *
     * @param resetPasswordRequest - код восстановления пароля и коды капчи
     * @return ResultErrorResponse
     * В случае, если все данные отправлены верно: "result": true
     * В случае ошибок: "result": false + "errors":
     */
    @Override
    public ResultErrorResponse setNewPassword(ResetPasswordRequest resetPasswordRequest) {
        ResultErrorResponse resultErrorResponse = new ResultErrorResponse();
        ErrorResponse.ErrorResponseBuilder errorResponseBuilder = ErrorResponse.builder();

        User user = userRepository.findByCode(resetPasswordRequest.getCode());

        if (!captchaCodeService.checkCaptcha(resetPasswordRequest.getCaptcha(), resetPasswordRequest.getCaptchaSecret())) {
            errorResponseBuilder.captcha("Код с картинки введён неверно");
        }

        if (!checkPassword(resetPasswordRequest.getPassword())) {
            errorResponseBuilder.password("Пароль короче 6-ти символов");
        }

        if ( user == null) {
            errorResponseBuilder.code("Ссылка для восстановления пароля устарела."
                           + " <a href="
                    + "\\\"/auth/restore\">Запросить ссылку снова</a>");
        }

        ErrorResponse errors = errorResponseBuilder.build();
        if (!errors.isPresent()) {
            user.setPassword(resetPasswordRequest.getPassword());
            user.setCode("");
            saveUser(user);
            resultErrorResponse.setResult(true);
        } else {
            resultErrorResponse.setErrors(errors);
        }

        return resultErrorResponse;
    }
}
