package com.urlshortener.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@AllArgsConstructor
public class ShortenResponse {

    @Schema(description = "단축 URL", example = "http://localhost:8080/abc123")
    private final String shortUrl;

    @Schema(description = "QR코드 base64", example = "iVBORw0KG...")
    private final String qrBase64;

    @Schema(description = "만료 일시", example = "2025-06-01T10:00:00")
    private final LocalDateTime expiredAt;
}
