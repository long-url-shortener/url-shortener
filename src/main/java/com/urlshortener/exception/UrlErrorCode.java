package com.urlshortener.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UrlErrorCode {
    NOT_FOUND("존재하지 않거나 비활성화된 코드입니다.", HttpStatus.NOT_FOUND, "/error/404.html"),
    EXPIRED  ("링크가 만료되었습니다.",             HttpStatus.GONE,    "/error/410.html"),
    DELETED("삭제처리된 링크입니다.",  HttpStatus.GONE,    "/error/410.html")
    ;

    private final String message;
    private final HttpStatus status;
    private final String viewPath;
}
