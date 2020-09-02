package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String code;

    private String password;

    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
