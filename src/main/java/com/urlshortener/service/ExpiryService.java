package com.urlshortener.service;

import com.urlshortener.repository.UrlRepository;
import com.urlshortener.entity.UrlEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpiryService {

    @Autowired
    private UrlRepository urlRepository;

    /**
     * 매시간 정각마다 실행: 활성 상태이면서 만료시간이 지난 URL을 비활성화
     * (cron 표현식: 초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireUrls() {
        LocalDateTime now = LocalDateTime.now();
        List<UrlEntity> toExpire = urlRepository.findAllByUseYnAndExpiredAtBefore("Y", now);

        // 빈 리스트면 saveAll 호출하지 않음
        if (!toExpire.isEmpty()) {
            toExpire.forEach(UrlEntity::deactivate);
            urlRepository.saveAll(toExpire);
        }
    }
}
