package com.kochetkova.api.response;

import lombok.Data;

import java.util.List;

@Data
public class TagWeightResponse {
    private List<TagResponse> tags;
}
