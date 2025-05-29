package com.urlshortener.controller;

import com.urlshortener.service.UrlService;
import com.urlshortener.dto.ShortenRequest;
import com.urlshortener.dto.ShortenResponse;
import com.urlshortener.entity.UrlEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@Tag(name = "URL 단축 API", description = "단축 URL 생성, 조회, 삭제")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @Operation(summary = "단축 URL 생성")
    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody ShortenRequest request) {
        ShortenResponse result = urlService.shortenUrl(request);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "단축 URL 리디렉션")
    @GetMapping("/{code:[A-Za-z0-9]{6}}") // 6자리 코드만 허용
    public ResponseEntity<?> redirect(@PathVariable String code) {
        UrlEntity urlEntity = urlService.getUrlByShortCode(code);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(urlEntity.getOriginalUrl()))
                .build();
    }


    @Operation(summary = "단축 URL 전체 조회")
    @GetMapping("/list")
    public List<UrlEntity> getAll() {
        return urlService.getAllUrls();
    }

    @Operation(summary = "단축 URL 논리 삭제 (useYn = 'N' 처리)")
    @PatchMapping("/admin/urls/{code}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable String code) {
        urlService.deleteByShortCode(code);
        return ResponseEntity.ok("비활성화 처리 완료");
    }

    @Operation(summary = "단축 URL 복구")
    @PatchMapping("/admin/restore/{code}")
    public ResponseEntity<?> restore(@PathVariable String code) {
        urlService.restoreByShortCode(code);
        return ResponseEntity.ok("복구 완료");
    }


    @Operation(summary = "단축 URL 목록 조회 (useYn 필터 포함)")
    @GetMapping("/admin/list")
    public ResponseEntity<Page<UrlEntity>> getPagedUrls(
            @RequestParam(defaultValue = "Y") String useYn,
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "shortCode", direction = Sort.Direction.ASC)
            }) Pageable pageable
    ) {
        Page<UrlEntity> result = urlService.getUrlPage(pageable, useYn);
        return ResponseEntity.ok(result);
    }






}
