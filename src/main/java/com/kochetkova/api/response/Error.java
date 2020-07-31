package com.kochetkova.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {
    private Boolean result;
    private String title;
    private String text;
    private String code;
    private String captcha;
    private String email;
    private String photo;
    private String name;
    private String password;
}
