package com.kochetkova.service;

import com.kochetkova.api.request.EditProfile;
import com.kochetkova.api.request.NewUser;
import com.kochetkova.api.response.Error;
import com.kochetkova.model.User;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    void saveUser(User user);
    boolean addNewUser(NewUser user);
    boolean isPresentUserByEmail(String email);
    User findUserByEmail(String email);
    User findUserById(Integer id);
    boolean checkUserData(NewUser newUser);
    boolean checkPassword(String password);
    boolean checkName(String name);
    boolean checkEmail(String email);
    Error checkEditProfile(User user, EditProfile editProfile);
    User saveEditProfile(User user, EditProfile editProfile);
    Error checkEditProfile(User user, String name, String email, String password, MultipartFile photo);
    User saveEditProfile(User user, String name, String email, String password, MultipartFile photo, Integer removePhoto);
    void saveSession(String sessionId,User user);
    void deleteSession(String sessionId);
    boolean findAuthSession(String sessionId);
    User findAuthUser(String sessionId);
    String savePhoto (User user, MultipartFile photo);
    String deletePhoto(User user);

}
