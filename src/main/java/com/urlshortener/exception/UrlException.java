package com.urlshortener.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class UrlException extends RuntimeException {
    private final UrlErrorCode errorCode;

    public UrlException(UrlErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
