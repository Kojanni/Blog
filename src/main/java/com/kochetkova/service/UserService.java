package com.kochetkova.service;

import com.kochetkova.api.request.NewUser;
import com.kochetkova.model.User;


public interface UserService {
    boolean addNewUser(NewUser user);
    boolean isPresentUserByEmail(String email);
    User findUserByEmail(String email);
    User findUserById(Integer id);
    boolean checkUserData(NewUser newUser);
    boolean checkPassword(String password);
    boolean checkName(String name);
    boolean checkEmail(String email);

}
