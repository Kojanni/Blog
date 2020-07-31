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
        if (checkUserData(newUser) && !isPresentUserByEmail(newUser.getEmail())) {
            User user = new User(newUser);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    @Override
    public User findUserById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    @Override
    public boolean isPresentUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    @Override
    public boolean checkUserData(NewUser user) {
        return user.getEmail().matches(EMAIL_REG)
                && user.getPassword().matches(PASSWORD_REG)
                && user.getName().matches(NAME_REG);
    }

    @Override
    public boolean checkPassword(String password) {
        return password.matches(PASSWORD_REG);
    }

    @Override
    public boolean checkName(String name) {
        return name.matches(NAME_REG);
    }

    @Override
    public boolean checkEmail(String email) {
        return email.matches(EMAIL_REG);
    }
}
