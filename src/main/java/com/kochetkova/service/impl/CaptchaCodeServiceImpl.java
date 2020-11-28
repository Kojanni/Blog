package com.kochetkova.service.impl;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.kochetkova.api.response.CaptchaResponse;
import com.kochetkova.model.CaptchaCode;
import com.kochetkova.repository.CaptchaCodeRepository;
import com.kochetkova.service.CaptchaCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Value("${captcha.length}")
    private int lengthSecretKey;

    @Value("${captcha.wordLength}")
    private int wordLength;

    @Value("${captcha.width}")
    private int width;

    @Value("${captcha.height}")
    private int height;

    @Value("${captcha.lifetime}")
    private int lifetime;


    @Autowired
    public CaptchaCodeServiceImpl(CaptchaCodeRepository captchaCodeRepository) {
        this.captchaCodeRepository = captchaCodeRepository;
    }

    @Override
    public CaptchaResponse getCaptcha() throws IOException {
        String token = generateToken(wordLength);
        String secretKey = generateSecretKey(lengthSecretKey);

        String encodedImage = encodeCaptcha(generateCaptcha(token));

        CaptchaResponse captcha = new CaptchaResponse();
        captcha.setImage(encodedImage);
        captcha.setSecret(secretKey);

        insertCaptchaInDB(token, secretKey);
        return captcha;
    }

    //Scheduled:
    //clear old captcha in DB (fixedRate in milliseconds)
    @Scheduled(fixedRateString = "${captcha.scheduledRate}")
    public void clearOldCaptcha() {
        captchaCodeRepository.deleteByTimeLessThanEqual(LocalDateTime.now().minusMinutes(lifetime));
    }

    @Override
    public boolean checkCaptcha(String captcha, String secretCode) {
        Optional<CaptchaCode> captchaCode = captchaCodeRepository.findBySecretCode(secretCode);
        return (captchaCode.isPresent() && captchaCode.get().getCode().equalsIgnoreCase(captcha));
    }

    private BufferedImage generateCaptcha(String token) throws IOException {
        Cage cage = new GCage();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        cage.draw(token, os);

        byte[] bytesImage = os.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytesImage);

        return resizeImage(ImageIO.read(bis));
    }

    private String generateSecretKey(int length) {
        int charStartOfNumb = '0';
        int charEndOfNumb = '9';
        int charStartOfBigLetter = 'A';
        int charEndOfBigLetter = 'Z';
        int charStartOfSmallLetter = 'a';
        int charEndOfSmallLetter = 'z';

        return (new Random()).ints(charStartOfNumb, charEndOfSmallLetter)
                .filter(i -> (i < charEndOfNumb || i > charStartOfBigLetter) && (i < charEndOfBigLetter || i > charStartOfSmallLetter))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private String generateToken(int length) {
        int charOne = '1';
        int charSmallI = 'i';
        int charBigI = 'I';
        int charSmallL = 'l';
        int charBigL = 'L';
        int charStartOfNumb = '0';
        int charEndOfNumb = '9';
        int charStartOfBigLetter = 'A';
        int charEndOfBigLetter = 'Z';
        int charStartOfSmallLetter = 'a';
        int charEndOfSmallLetter = 'z';

        return (new Random()).ints(charStartOfNumb, charEndOfSmallLetter)
                .filter(i -> (i < charEndOfNumb || i > charStartOfBigLetter) && (i < charEndOfBigLetter || i > charStartOfSmallLetter)
                        && (i != charOne) && (i != charSmallI) && (i != charBigI) && (i != charSmallL) && (i != charBigL))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private BufferedImage resizeImage(BufferedImage bImage) {
        Scalr.Method method = Scalr.Method.ULTRA_QUALITY;
        Scalr.Mode mode = Scalr.Mode.FIT_EXACT;
        return Scalr.resize(bImage, method, mode, width, height);
    }

    private String encodeCaptcha(BufferedImage image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytesResizedImg = bos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytesResizedImg);
    }


    private void insertCaptchaInDB(String code, String secretCode) {
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(code);
        captchaCode.setSecretCode(secretCode);
        captchaCode.setTime(LocalDateTime.now());
        captchaCodeRepository.save(captchaCode);
    }
}
