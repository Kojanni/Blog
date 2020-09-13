package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kochetkova.model.GlobalSetting;
import lombok.Data;

import java.util.List;

@Data
public class SettingsRequest {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiuserMode;

    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;

}
