package com.kochetkova.service.impl;

import com.kochetkova.api.request.NewUser;
import com.kochetkova.model.User;
import com.kochetkova.repository.UserRepository;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private static final String EMAIL_REG = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$";
    private static final String NAME_REG = "[A-ZА-Я][a-zа-я]+";
    private static Map<String, Integer> sessions = new HashMap<>();

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
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
                && user.getPassword().length() >= 6
                && user.getName().matches(NAME_REG);
    }

    @Override
    public boolean checkPassword(String password) {
        return password.length() >= 6;
    }

    @Override
    public boolean checkName(String name) {
        return name.matches(NAME_REG);
    }

    @Override
    public boolean checkEmail(String email) {
        return email.matches(EMAIL_REG);
    }

    @Override
    public void saveSession(String sessionId, int userId) {
        sessions.put(sessionId, userId);
    }

    @Override
    public boolean findAuthSession(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    @Override
    public User findAuthUser(String sessionId) {
        return findUserById(sessions.get(sessionId));
    }
}
