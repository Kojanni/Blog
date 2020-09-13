package com.kochetkova.service.impl;

import com.kochetkova.api.request.SettingsRequest;
import com.kochetkova.api.response.SettingsResponse;
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

    /**
     * Получение списка с данными о каждой настройке
     *
     * @return List<GlobalSetting> Метод возвращает список данных глобальных настройек блога
     */
    @Override
    public List<GlobalSetting> getAll() {
        List<GlobalSetting> settings = new ArrayList();

        settingsRepository.findAll().forEach(settings::add);
        return settings;
    }

    /**
     * Получение настроек
     *
     * @return SettingsResponse Метод возвращает глобальные настройки блога из таблицы global_settings.
     */
    @Override
    public SettingsResponse getSettings() {
        return getSettings(getAll());
    }

    /**
     * Сохранение настроек
     * Метод записывает глобальные настройки блога в таблицу global_settings
     *
     * @param settingsRequest - данные о изменении настроек
     */
    @Override
    public void saveSettings(SettingsRequest settingsRequest) {
        SettingsResponse settingsResponse;

        List<GlobalSetting> settings = getGlobalSettingsList(settingsRequest);
        settingsRepository.saveAll(settings);


    }

    /**
     * Получение настроек по данным листа настройек из БД
     *
     * @param globalSettings - лист с данными по настройкам из БД
     * @return SettingsResponse - получение настроек и значения
     */
    private SettingsResponse getSettings(List<GlobalSetting> globalSettings) {
        SettingsResponse settings = new SettingsResponse();
        globalSettings.forEach(globalSetting -> {
            if (globalSetting.getCode().matches("MULTIUSER_MODE") && globalSetting.getValue().matches("YES")) {
                settings.setMultiuserMode(true);
            }
            if (globalSetting.getCode().matches("POST_PREMODERATION") && globalSetting.getValue().matches("YES")) {
                settings.setPostPremoderation(true);
            }
            if (globalSetting.getCode().matches("STATISTICS_IS_PUBLIC") && globalSetting.getValue().matches("YES")) {
                settings.setStatisticsIsPublic(true);
            }
        });
        return settings;
    }

    /**
     * Получение листа с данными по настройкам из запроса
     *
     * @param settingsRequest - настройки и их значения
     * @return List<GlobalSetting> - лист с данными по настройкам из БД
     */
    private List<GlobalSetting> getGlobalSettingsList(SettingsRequest settingsRequest) {
        List<GlobalSetting> settings = new ArrayList();
        settingsRepository.findAll().forEach(settings::add);

        settings.forEach(globalSetting -> {
            if (globalSetting.getCode().equalsIgnoreCase("MULTIUSER_MODE")) {
                if (settingsRequest.isMultiuserMode()) {
                    globalSetting.setValue("YES");
                } else {
                    globalSetting.setValue("NO");
                }
            }
            if (globalSetting.getCode().equalsIgnoreCase("POST_PREMODERATION")) {
                if (settingsRequest.isPostPremoderation()) {
                    globalSetting.setValue("YES");
                } else {
                    globalSetting.setValue("NO");
                }
            }
            if (globalSetting.getCode().equalsIgnoreCase("STATISTICS_IS_PUBLIC")) {
                if (settingsRequest.isStatisticsIsPublic()) {
                    globalSetting.setValue("YES");
                } else {
                    globalSetting.setValue("NO");
                }
            }
        });
        return settings;
    }
}
