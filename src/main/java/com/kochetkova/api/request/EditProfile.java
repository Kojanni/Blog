package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditProfile {
    private String name;
    private String email;
    private String password;
    private String photo;
    private Integer removePhoto;
}
