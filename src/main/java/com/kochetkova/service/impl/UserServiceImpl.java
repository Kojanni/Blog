package com.kochetkova.service.impl;

import com.kochetkova.api.request.NewUser;
import com.kochetkova.model.User;
import com.kochetkova.repository.UserRepository;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private static final String EMAIL_REG = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$";

    //не менее 6 символов, Содержит хотя бы одну цифру, хотя бы один нижний и один верхний char, хотя бы один char в наборе специальных символов (@#%$^ и т.д.), не содержит пробелов, вкладок и т.д.
    private static final String PASSWORD_REG = "^.*(?=.{6,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!~#$%^&+=]).*$";
    private static final String NAME_REG = "[A-ZА-Я][a-zа-я]+";

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean addNewUser(NewUser newUser) {
        if (checkUserData(newUser) && !isPresent(newUser)) {
            User user = new User(newUser);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPresent(NewUser newUser) {
        Optional<User> user = userRepository.findByEmail(newUser.getEmail());
        if (user.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkUserData(NewUser user) {
        if (user.getEmail().matches(EMAIL_REG)
                && user.getPassword().matches(PASSWORD_REG)
                && user.getName().matches(NAME_REG) ) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkPassword(String password) {
        if ( password.matches(PASSWORD_REG)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkName(String name) {
        if ( name.matches(NAME_REG) ) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkEmail(String email) {
        if (email.matches(EMAIL_REG)) {
            return true;
        }
        return false;
    }
}
