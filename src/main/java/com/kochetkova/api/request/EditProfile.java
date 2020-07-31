package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditProfile {
    private String name;
    private String email;
    private String password;
    private String photo;
    private Integer removePhoto;
}
