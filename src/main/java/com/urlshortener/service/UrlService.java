package com.urlshortener.service;

import com.urlshortener.dto.ShortenRequest;
import com.urlshortener.dto.ShortenResponse;
import com.urlshortener.entity.UrlEntity;
import com.urlshortener.exception.UrlErrorCode;
import com.urlshortener.exception.UrlException;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.util.CodeGenerator;
import com.urlshortener.util.QRCodeGenerator;
import org.springframework.data.domain.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    public ShortenResponse shortenUrl(ShortenRequest request) {
        String code;
        do {
            code = CodeGenerator.generateRandomCode(6);
        } while (urlRepository.existsByShortCodeAndUseYn(code,"Y"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusDays(1); // 생성 시간 기준 +1일
        int clickCount = 0;

        UrlEntity urlEntity = new UrlEntity(code, request.getUrl(), now, expiredAt, clickCount);
        urlRepository.save(urlEntity);

        String shortUrl = "http://localhost:8080/" + code;
        String qrBase64;
        try {
            qrBase64 = QRCodeGenerator.generateQRCodeImage(shortUrl);
        } catch (Exception e) {
            throw new RuntimeException("QR 코드 생성 실패", e);
        }

        return new ShortenResponse(shortUrl, qrBase64, expiredAt);
    }


    public UrlEntity getUrlByShortCode(String code) {
        UrlEntity urlEntity = urlRepository
                .findByShortCodeAndUseYn(code, "Y")
                .orElseThrow(() -> new UrlException(UrlErrorCode.NOT_FOUND));

        // 삭제된(useYn='N') 항목이면 410으로
        if (!"Y".equals(urlEntity.getUseYn())) {
            throw new UrlException(UrlErrorCode.DELETED);
        }

        // 만료일 지나면 410으로
        if (urlEntity.getExpiredAt() != null
                && urlEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new UrlException(UrlErrorCode.EXPIRED);
        }

        urlEntity.increaseClickCount();
        urlRepository.save(urlEntity);
        return urlEntity;
    }

    public List<UrlEntity> getAllUrls() {
        return urlRepository.findAll();
    }

    // 삭제 기능
    public void deleteByShortCode(String code) {
        UrlEntity urlEntity = getUrlByShortCode(code);
        urlEntity.deactivate();
        urlRepository.save(urlEntity);     // 실제 삭제는 x 상태만 변경
    }

    public void restoreByShortCode(String code) {
        UrlEntity urlEntity = urlRepository.findByShortCodeAndUseYn(code, "N")
                .orElseThrow(() -> new RuntimeException("이미 사용 중이거나 존재하지 않는 코드입니다."));
        urlEntity.activate();
        urlRepository.save(urlEntity);
    }


    public Page<UrlEntity> getUrlPage(Pageable pageable, String useYn) {
        if (useYn == null || useYn.equalsIgnoreCase("Y") || useYn.equalsIgnoreCase("N")) {
            return urlRepository.findAllByUseYn(useYn.toUpperCase(), pageable);
        }
        return urlRepository.findAll(pageable); // useYn = "ALL" 또는 잘못된 값 → 전체
    }


}

