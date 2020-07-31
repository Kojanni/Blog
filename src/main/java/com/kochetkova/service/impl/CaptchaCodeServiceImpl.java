package com.kochetkova.service.impl;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.kochetkova.api.response.Captcha;
import com.kochetkova.model.CaptchaCode;
import com.kochetkova.repository.CaptchaCodeRepository;
import com.kochetkova.service.CaptchaCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import org.imgscalr.Scalr;

@Service
public class CaptchaCodeServiceImpl implements CaptchaCodeService {
    private final CaptchaCodeRepository captchaCodeRepository;
    private static int LENGTH_SK = 26;
    private static int WIDTH = 120;
    private static int HEIGHT = 42;



    @Value("${db.captcha.lifetime}")
    private int lifetimeMinutes;


    @Autowired
    public CaptchaCodeServiceImpl(CaptchaCodeRepository captchaCodeRepository) {
        this.captchaCodeRepository = captchaCodeRepository;
    }

    @Override
    public Captcha getCaptcha() throws IOException {
        clearOldCaptcha();

        Captcha captcha = new Captcha();
        Cage cage = new GCage();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        String token = cage.getTokenGenerator().next();
        String secretKey = getSecretKey(LENGTH_SK);

        try {
            cage.draw(token, os);

            byte[] bytesImage = os.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(bytesImage);

            BufferedImage bImage = ImageIO.read(bis);
            BufferedImage resizedImg = Scalr.resize(bImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, WIDTH, HEIGHT, Scalr.OP_BRIGHTER);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(resizedImg, "png", bos );
            byte [] bytesResizedImg = bos.toByteArray();
            String encodedImage = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytesResizedImg);

            captcha.setImage(encodedImage);
            captcha.setSecret(secretKey);
        } finally {
            os.close();
        }

        insertCaptchaInDB(token, secretKey);
        return captcha;
    }

    @Override
    public boolean checkCaptcha(String captcha, String secretCode) {
        Optional<CaptchaCode> captchaCode = captchaCodeRepository.findBySecretCode(secretCode);
        if (captchaCode.isPresent() && captchaCode.get().getCode().matches(captcha)) {
            return true;
        }
        return false;
    }

    private String getSecretKey(int length) {
       return  (new Random()).ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private void insertCaptchaInDB(String code, String secretCode){
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(code);
        captchaCode.setSecretCode(secretCode);
        captchaCode.setTime(LocalDateTime.now());
        captchaCodeRepository.save(captchaCode);
    }

    private void clearOldCaptcha() {
        List<CaptchaCode> existedCaptcha = new ArrayList<>();
        captchaCodeRepository.findAll().forEach(existedCaptcha::add);
        captchaCodeRepository.deleteByTimeLessThanEqual(LocalDateTime.now().minusMinutes(lifetimeMinutes));
    }
}
