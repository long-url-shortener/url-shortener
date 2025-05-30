package com.urlshortener.service;

import com.urlshortener.entity.UrlEntity;
import com.urlshortener.repository.UrlRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class GenerateService {

    private static final long TOTAL = (long) Math.pow(36, 6);
//    private static final int BATCH_SIZE = 1_000;
    private static final int BATCH_SIZE = 100;

    private final UrlRepository urlRepository;

    public GenerateService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

//    @Async               // 스프링의 TaskExecutor를 쓰면 별도 스레드에서 실행
////    @Transactional
//    public void generateAllCodes() {
//        // 0부터 TOTAL-1까지 순회하며 base36으로 인코딩
//        for (long start = 0; start < TOTAL; start += BATCH_SIZE) {
//            long end = Math.min(TOTAL, start + BATCH_SIZE);
//
//            List<UrlEntity> batch = LongStream.range(start, end)
//                .mapToObj(this::toUrlEntity)
//                .collect(Collectors.toList());
//
//            urlRepository.saveAll(batch);
//            urlRepository.flush();  // 배치 단위로 DB에 쌓아 두지 않고 즉시 밀어넣기
//        }
//    }


@Async
public void generateAllCodes() {
    for (long start = 0; start < TOTAL; start += BATCH_SIZE) {
        long end = Math.min(TOTAL, start + BATCH_SIZE);

        // 1) 배치 단위로 엔티티 생성
        List<UrlEntity> batch = LongStream.range(start, end)
                .mapToObj(this::toUrlEntity)
                .collect(Collectors.toList());

        // 2) DB에 이미 있는 코드는 필터링
        List<UrlEntity> filtered = batch.stream()
                .filter(u -> !urlRepository.existsByShortCodeAndUseYn(u.getShortCode(), "Y"))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            continue;
        }

        // 3) 저장 시 중복 예외는 무시
        try {
            urlRepository.saveAll(filtered);
            urlRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // ON CONFLICT DO NOTHING 효과: 이미 있는 건은 건너뜁니다.
        }
    }
}

    private UrlEntity toUrlEntity(long seq) {
        String code = toBase36Padded(seq, 6);
        return new UrlEntity(
            code,
            "",                      // 실제 originalUrl은 빈값 또는 플레이스홀더
            LocalDateTime.now(),
            null,                    // 만료일 없음
            0                        // 클릭수
        );
    }

    // 10진수 → 36진수, 고정 6자리(왼쪽 0 채움)
//    0–9와 a–z(영어 소문자)만을 사용해 36진수 문자열을 생성
    private String toBase36Padded(long value, int length) {
        String s = Long.toString(value, 36);
        return String.format("%" + length + "s", s).replace(' ', '0');
    }
}
