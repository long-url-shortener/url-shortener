package com.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "URL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UrlEntity {

//    @Id
//    private String shortCode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQUENCE")
    private Long sequence;

    @Column(name = "SHORT_CODE", nullable = false, unique = true)
    private String shortCode;

    @Column(name = "ORIGINAL_URL", nullable = false)
    private String originalUrl;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "EXPIRED_AT")
    private LocalDateTime expiredAt;

    @Column(name = "USE_YN", nullable = false, length = 1)
    private String useYn = "Y";

    @Column(name = "CLICK_COUNT", nullable = false)
    private int clickCount = 0;

    public void increaseClickCount() {
        this.clickCount++;
    }


    public UrlEntity(String shortCode, String originalUrl, LocalDateTime createdAt, LocalDateTime expiredAt, int clickCount) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.clickCount = clickCount;
    }

    public void deactivate() {
        this.useYn = "N";
    }

    public void activate() {
        this.useYn = "Y";
    }
}
