package com.urlshortener.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ShortenRequest {

    @Schema(description = "단축할 원본 URL", example = "https://naver.com", required = true)
    private String url;
}
