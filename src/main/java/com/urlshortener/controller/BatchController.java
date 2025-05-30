//package com.urlshortener.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/batch")
//@RequiredArgsConstructor
//public class BatchController {
//
//    private final JobLauncher jobLauncher;
//    private final Job generateAllCodesJob;
//
//    @Operation(summary = "모든 가능한 6자리 코드 일괄 생성 배치 실행")
//    @PostMapping("/generate-all-codes")
//    public ResponseEntity<String> runGenerateAllCodes() throws Exception {
//        jobLauncher.run(
//            generateAllCodesJob,
//            new JobParametersBuilder()
//                .addLong("timestamp", System.currentTimeMillis())
//                .toJobParameters()
//        );
//        return ResponseEntity.accepted()
//                             .body("배치 작업이 시작되었습니다.");
//    }
//}
