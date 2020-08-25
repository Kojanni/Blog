package com.kochetkova.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kochetkova.model.GlobalSetting;
import lombok.Data;

import java.util.List;

@Data
public class SettingsResponse {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiuserMode;

    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;

    public void getSettings(List<GlobalSetting> globalSettings) {
        globalSettings.forEach(globalSetting -> {
            if (globalSetting.getCode().matches("MULTIUSER_MODE") && globalSetting.getValue().matches("YES")) {
                multiuserMode = true;
            }
            if (globalSetting.getCode().matches("POST_PREMODERATION") && globalSetting.getValue().matches("YES")) {
                postPremoderation = true;
            }
            if (globalSetting.getCode().matches("STATISTICS_IS_PUBLIC") && globalSetting.getValue().matches("YES")) {
                statisticsIsPublic = true;
            }
        });
    }
}
