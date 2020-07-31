package com.kochetkova.service.impl;

import com.kochetkova.model.GlobalSetting;
import com.kochetkova.repository.SettingsRepository;
import com.kochetkova.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public List<GlobalSetting> getAll() {
        List<GlobalSetting> settings = new ArrayList();
        settingsRepository.findAll().forEach(settings::add);
        return settings;
    }


    @Override
    public void editSetting(String code, boolean value) {

    }

    @Override
    public GlobalSetting getByName(String code) {
        return null;
    }
}
