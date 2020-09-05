package com.kochetkova.service.impl;

import com.kochetkova.api.request.EditProfileRequest;
import com.kochetkova.api.request.NewUserRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.UserResponse;
import com.kochetkova.model.User;
import com.kochetkova.repository.UserRepository;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private static final String EMAIL_REG = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$";
    private static final String NAME_REG = "[A-ZА-Яa-zа-я]+";
    private final String separator = File.separator;
    private static Map<String, Integer> sessions = new HashMap<>();

    @Value("${photo.path}")
    private String photoPath;

    @Value("${password.length.min}")
    private int passwordLengthMin;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.findByEmail(email).orElse(null);
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

    //сохранить данные пользователя для обновления профиля запрос с фото
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
            user.setPhoto(deletePhoto(user));
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
        return findUserById(sessions.get(sessionId));
    }

    /**
     *  cохранение новой фотографии в папке и установка ссылки у пользователя
     */
    @Override
    public String savePhoto(User user, MultipartFile photo) {
        if (!photo.isEmpty()) {
            int id = user.getId();

            int numberOfFolder = id / 100;
            String fullPath = photoPath + separator + numberOfFolder + separator + id + "_" + photo.getOriginalFilename();
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                FileCopyUtils.copy(photo.getBytes(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "/" + fullPath.replace("\\", "/");
        }
        return null;
    }

    /**
     * удаление фотографии из папки и ссылки у пользователя
     */
    @Override
    public String deletePhoto(User user) {
        String fullPath = user.getPhoto();
        File file = new File(fullPath.substring(1).replace("/", "\\"));
        file.delete();
        user.setPhoto("");
        userRepository.save(user);
        return null;
    }

    /**
     * создание нового юзера по введенным данным в формате NewUser
     */
    @Override
    public User createNewUser(NewUserRequest newUser) {
        User user = new User();
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());
        user.setIsModerator((byte) 0);
        user.setRegTime(LocalDateTime.now());

        return user;
    }

    /**
     * формирование UserResponse на основе User
     * @param user - информация, полученная из БД, о пользователе
     * @param mode - режим полноты выдачи информации:
     *             1 - только id и name;
     *             2 - id, name, photo
     * @return объект класса UserResponse
     */
    @Override
    public UserResponse createUserResponse(User user, int mode) {
        UserResponse.UserResponseBuilder userResponseBuilder = UserResponse.builder();
        if (mode >= 1) {
            userResponseBuilder.id(user.getId());
            userResponseBuilder.name(user.getName());
        }
        if (mode >= 2) {
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
        userResponseBuilder.moderationCount(userInfo.getModerationPosts().size());
        return userResponseBuilder.build();
    }
}
