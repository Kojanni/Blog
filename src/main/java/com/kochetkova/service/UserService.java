package com.kochetkova.service;

import com.kochetkova.api.request.NewUser;

public interface UserService {
    boolean addNewUser(NewUser user);
    boolean isPresent(NewUser newUser);
    boolean checkUserData(NewUser newUser);
    boolean checkPassword(String password);
    boolean checkName(String name);
    boolean checkEmail(String email);

}
