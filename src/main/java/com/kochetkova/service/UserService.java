package com.kochetkova.service;

import com.kochetkova.api.request.EditProfileRequest;
import com.kochetkova.api.request.NewUserRequest;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.model.User;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
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
    void saveSession(String sessionId,User user);
    void deleteSession(String sessionId);
    boolean findAuthSession(String sessionId);
    User findAuthUser(String sessionId);
    String savePhoto (User user, MultipartFile photo);
    String deletePhoto(User user);
    User createNewUser(NewUserRequest newUser);
}
