package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonProperty("e_mail")
    private String email;

    private String password;

    public String getEmail() {
        return email;
    }
}
