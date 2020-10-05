package com.kochetkova.service;

import com.kochetkova.api.request.EditProfileRequest;
import com.kochetkova.api.request.NewUserRequest;
import com.kochetkova.api.request.ResetPasswordRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.api.response.ResultErrorResponse;
import com.kochetkova.api.response.UserResponse;
import com.kochetkova.model.User;
import com.kochetkova.service.impl.enums.ModeUserInfo;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    User auth(String email, String password);

    void saveUser(User user);

    boolean addNewUser(NewUserRequest user);

    boolean isPresentUserByEmail(String email);

    User findUserByEmail(String email);

    User findUserById(Integer id);

    boolean checkUserData(NewUserRequest newUser);

    boolean checkRegisteredUserData(NewUserRequest newUser);

    boolean checkPassword(String password);

    boolean checkName(String name);

    boolean checkEmail(String email);

    ErrorResponse checkEditProfile(User user, EditProfileRequest editProfile);

    User saveEditProfile(User user, EditProfileRequest editProfile);

    ErrorResponse checkEditProfile(User user, String name, String email, String password, MultipartFile photo);

    User saveEditProfile(User user, String name, String email, String password, MultipartFile photo, Integer removePhoto);

    void saveSession(String sessionId, User user);

    void deleteSession(String sessionId);

    boolean findAuthSession(String sessionId);

    User findAuthUser(String sessionId);

    String savePhoto(User user, MultipartFile photo);

    boolean deletePhoto(User user);

    User createNewUser(NewUserRequest newUser);

    UserResponse createUserResponse(User user, ModeUserInfo mode);

    UserResponse createUserResponse(User userInfo);

    ResultErrorResponse restorePassword(String email);

    ResultErrorResponse setNewPassword(ResetPasswordRequest resetPasswordRequest);
}
