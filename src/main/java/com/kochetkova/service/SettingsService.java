package com.kochetkova.service;

import com.kochetkova.model.GlobalSetting;

import java.util.List;

public interface SettingsService {
    List<GlobalSetting> getAll();
    void editSetting(String code, boolean value);
    GlobalSetting getByName(String code);
}
