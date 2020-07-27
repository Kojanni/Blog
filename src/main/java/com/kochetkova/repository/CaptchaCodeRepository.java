package com.kochetkova.repository;

import com.kochetkova.model.CaptchaCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CaptchaCodeRepository extends CrudRepository<CaptchaCode, Integer> {
    @Transactional
    void deleteByTimeLessThanEqual(LocalDateTime lifetime);

    List<CaptchaCode> findByTimeLessThanEqual(LocalDateTime lifetime);
}
