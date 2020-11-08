package com.kochetkova.service;

import com.kochetkova.api.response.CaptchaResponse;

import java.io.IOException;

public interface CaptchaCodeService {
    CaptchaResponse getCaptcha() throws IOException;
    boolean checkCaptcha(String captcha, String secretCode);
    void clearOldCaptcha();
}
