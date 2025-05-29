package com.urlshortener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.urlshortener.entity.UrlEntity;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.service.ExpiryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpiryServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private ExpiryService expiryService;

    @Test
    void 만료된_URL을_비활성화한다() {
        // Given
        UrlEntity expiredUrlEntity = new UrlEntity("abc123", "http://localhost:8080/", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusHours(1), 0);
        // url.useYn defaults to "Y"

        when(urlRepository.findAllByUseYnAndExpiredAtBefore(eq("Y"), any(LocalDateTime.class)))
            .thenReturn(List.of(expiredUrlEntity));

        // When
        expiryService.expireUrls();

        // Then
        assertEquals("N", expiredUrlEntity.getUseYn());
        verify(urlRepository).saveAll(List.of(expiredUrlEntity));
    }

    @Test
    void 만료된_URL이_없으면_저장하지_않는다() {
        // Given
        when(urlRepository.findAllByUseYnAndExpiredAtBefore(eq("Y"), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        // When
        expiryService.expireUrls();

        // Then
        verify(urlRepository, never()).saveAll(any());
    }
}
