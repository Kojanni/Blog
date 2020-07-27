package com.kochetkova.service;

import com.kochetkova.api.response.Captcha;

import java.io.IOException;

public interface CaptchaCodeService {
    Captcha getCaptcha() throws IOException;

}
