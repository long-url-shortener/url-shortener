//package com.urlshortener.service;
//
//import com.urlshortener.entity.UrlEntity;
//import com.urlshortener.repository.UrlRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.stream.Collectors;
//import java.util.stream.LongStream;
//
//@Slf4j
//@Service
//public class GenerateService {
//
//    private static final long TOTAL = (long) Math.pow(36, 6);
////    private static final int BATCH_SIZE = 1_000;
//    private static final int BATCH_SIZE = 1000;
//
//    private final UrlRepository urlRepository;
//    private final AtomicLong processedCount = new AtomicLong(0);
//    private long startTime;
//
//
//    public GenerateService(UrlRepository urlRepository) {
//        this.urlRepository = urlRepository;
//    }
//
////
////@Async
////public void generateAllCodes() {
////    for (long start = 0; start < TOTAL; start += BATCH_SIZE) {
////        long end = Math.min(TOTAL, start + BATCH_SIZE);
////
////        // 1) 배치 단위로 엔티티 생성
////        List<UrlEntity> batch = LongStream.range(start, end)
////                .mapToObj(this::toUrlEntity)
////                .collect(Collectors.toList());
////
////        // 2) DB에 이미 있는 코드는 필터링
//////        List<UrlEntity> filtered = batch.stream()
//////                .filter(u -> !urlRepository.existsByShortCodeAndUseYn(u.getShortCode(), "Y"))
//////                .collect(Collectors.toList());
//////
//////        if (filtered.isEmpty()) {
//////            continue;
//////        }
////
////        // 3) 저장 시 중복 예외는 무시
////        urlRepository.saveAll(batch);
////        urlRepository.flush();
//////        try {
//////            urlRepository.saveAll(batch);
//////            urlRepository.flush();
//////        } catch (DataIntegrityViolationException e) {
//////            // ON CONFLICT DO NOTHING 효과: 이미 있는 건은 건너뜁니다.
//////        }
////    }
////}
//
//public void generateAllCodesParallel() {
//    startTime = System.currentTimeMillis();
//    processedCount.set(0);
//    log.info("시작: 총 레코드 수={} 건, 배치 크기={} 건", TOTAL, BATCH_SIZE);
//
//    List<CompletableFuture<Void>> futures = new ArrayList<>();
//    for (long start = 0; start < TOTAL; start += BATCH_SIZE) {
//        long end = Math.min(TOTAL, start + BATCH_SIZE);
//        futures.add(processBatch(start, end));
//    }
//    // 모든 배치가 완료될 때까지 대기
//    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//    long totalTime = System.currentTimeMillis() - startTime;
//    log.info("완료: 총 {} 건 처리, 전체 소요 시간={} ms", processedCount.get(), totalTime);
//}
//
//    /**
//     * 청크 단위로 실행되는 비동기 메서드.
//     * REQUIRES_NEW 트랜잭션으로 커밋 단위를 분리합니다.
//     */
//    @Async("batchTaskExecutor")
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public CompletableFuture<Void> processBatch(long start, long end) {
//        List<UrlEntity> batch = LongStream.range(start, end)
//                .mapToObj(this::toUrlEntity)
//                // 이미 활성화된 코드는 미리 필터링
//                .filter(u -> !urlRepository.existsByShortCodeAndUseYn(u.getShortCode(), "Y"))
//                .collect(Collectors.toList());
//
//        if (!batch.isEmpty()) {
//            try {
//                urlRepository.saveAll(batch);
//                urlRepository.flush();
//            } catch (DataIntegrityViolationException e) {
//                // 중복 발생 시 무시
//            }
//        }
//        return CompletableFuture.completedFuture(null);
//    }
//
//
//
//
//    private UrlEntity toUrlEntity(long seq) {
//        String code = toBase36Padded(seq, 6);
//        return new UrlEntity(
//            code,
//            "",                      // 실제 originalUrl은 빈값 또는 플레이스홀더
//            LocalDateTime.now(),
//            null,                    // 만료일 없음
//            0                        // 클릭수
//        );
//    }
//
//    // 10진수 → 36진수, 고정 6자리(왼쪽 0 채움)
////    0–9와 a–z(영어 소문자)만을 사용해 36진수 문자열을 생성
//    private String toBase36Padded(long value, int length) {
//        String s = Long.toString(value, 36);
//        return String.format("%" + length + "s", s).replace(' ', '0');
//    }
//}
