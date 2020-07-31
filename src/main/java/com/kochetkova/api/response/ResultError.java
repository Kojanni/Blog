package com.kochetkova.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultError {
    private boolean result;
    private Error errors;
}
