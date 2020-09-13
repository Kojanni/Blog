package com.kochetkova.service;

import com.kochetkova.api.request.SettingsRequest;
import com.kochetkova.api.response.SettingsResponse;
import com.kochetkova.model.GlobalSetting;

import java.util.List;

public interface SettingsService {
    List<GlobalSetting> getAll();

    SettingsResponse getSettings();

    void saveSettings(SettingsRequest settingsRequest);
}
