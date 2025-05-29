package com.urlshortener.repository;

import com.urlshortener.entity.UrlEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
//    Optional<Url> findByShortCode(String shortCode);
//    boolean existsByShortCode(String shortCode);
//    Page<Url> findAll(Pageable pageable);

    Optional<UrlEntity> findByShortCodeAndUseYn(String shortCode, String useYn);
    boolean existsByShortCodeAndUseYn(String shortCode, String useYn);
    Page<UrlEntity> findAllByUseYn(String useYn, Pageable pageable);
    Page<UrlEntity> findAll(Pageable pageable); // 전체 조회
    List<UrlEntity> findAllByUseYnAndExpiredAtBefore(String useYn, LocalDateTime dateTime);

}
