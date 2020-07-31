package com.kochetkova.repository;

import com.kochetkova.model.GlobalSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SettingsRepository extends CrudRepository<GlobalSetting, Integer> {

}
