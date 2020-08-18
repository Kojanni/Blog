package com.kochetkova.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {
    Boolean result;
    String title;
    String text;
    String code;
    String captcha;
    String email;
    String photo;
    String name;
    String password;

    @JsonIgnore
    public boolean isPresent(){
        return (title != null ||
                text!= null ||
                code!= null ||
                captcha!= null ||
                email!= null ||
                photo!= null ||
                name!= null ||
                password != null);
    }
}
