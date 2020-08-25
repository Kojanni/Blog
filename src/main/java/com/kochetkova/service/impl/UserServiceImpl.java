package com.kochetkova.service.impl;

import com.kochetkova.api.request.EditProfile;
import com.kochetkova.api.request.NewUser;
import com.kochetkova.api.response.ErrorResponse;
import com.kochetkova.model.User;
import com.kochetkova.repository.UserRepository;
import com.kochetkova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        return checkEmail(user.getEmail())
                && checkPassword(user.getPassword())
                && checkName(user.getName());
    }

    @Override
    public boolean checkPassword(String password) {
        return password.length() >= passwordLengthMin;
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

    @Override
    public ErrorResponse checkEditProfile(User user, EditProfile editProfile) {
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

    @Override
    public User saveEditProfile(User user, EditProfile editProfile) {
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

    @Override
    public String savePhoto(User user, MultipartFile photo) {
        if (!photo.isEmpty()) {
            int id = user.getId();

//            Blob blob = null;
//            String str = null;
//            StringBuffer strOut = new StringBuffer();
//            try {
//                byte[] bytes = photo.getBytes();
//                blob = new SerialBlob(bytes);
//                BufferedReader br = new BufferedReader(new InputStreamReader(blob.getBinaryStream()));
//                while ((str=br.readLine())!=null) {
//                    strOut.append(str);
//                }
//                return strOut.toString();
//            } catch (SQLException | IOException throwables) {
//                throwables.printStackTrace();
//            }
//            System.out.println(blob.toString() + blob );
//            System.out.println(str);



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

            return fullPath.replace("\\", "/");
        }
        return null;
    }

    @Override
    public String deletePhoto(User user) {
        String fullPath = user.getPhoto();
        File file = new File(fullPath);
        file.delete();
        return null;
    }
}
