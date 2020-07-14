package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResetPassword {

    private String code;

    private String password;

    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaSecret() {
        return captchaSecret;
    }

    public void setCaptchaSecret(String captchaSecret) {
        this.captchaSecret = captchaSecret;
    }
}
