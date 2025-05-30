package com.urlshortener.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlException.class)
    public ModelAndView handleUrlException(UrlException ex) {
        UrlErrorCode code = ex.getErrorCode();
        // 상태코드 설정을 위해 ModelAndView#setStatus
        ModelAndView mv = new ModelAndView("forward:" + code.getViewPath());
        mv.setStatus(code.getStatus());
        return mv;
    }
}
