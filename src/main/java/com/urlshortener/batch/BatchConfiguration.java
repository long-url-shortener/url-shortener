//package com.urlshortener.batch;
//
//import com.urlshortener.entity.UrlEntity;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//import java.time.LocalDateTime;
//import java.util.stream.LongStream;
//
//@Configuration
//@EnableBatchProcessing
//public class BatchConfiguration {
//
//    private static final long MAX_CODE = (long) Math.pow(36, 6) - 1;
//    private static final int CHUNK_SIZE = 10_000;
//
//    @Bean
//    public Job generateAllCodesJob(JobBuilderFactory jobs, Step generateStep) {
//        return jobs.get("generateAllCodesJob")
//                .start(generateStep)
//                .build();
//    }
//
//    @Bean
//    public Step generateStep(StepBuilderFactory steps,
//                             ItemReader<Long> reader,
//                             ItemProcessor<Long, UrlEntity> processor,
//                             ItemWriter<UrlEntity> writer) {
//        return steps.get("generateStep")
//                .<Long, UrlEntity>chunk(CHUNK_SIZE)
//                .reader(reader)
//                .processor(processor)
//                .writer(writer)
//                .build();
//    }
//
//    @Bean
//    public ItemReader<Long> rangeItemReader() {
//        return new ItemReader<>() {
//            private long next = 0;
//            @Override
//            public Long read() {
//                if (next > MAX_CODE) {
//                    return null;
//                }
//                return next++;
//            }
//        };
//    }
//
//    @Bean
//    public ItemProcessor<Long, UrlEntity> urlProcessor() {
//        return value -> {
//            // 36진수, 고정 6자리(왼쪽 0 채움)
//            String code = Long.toString(value, 36);
//            code = String.format("%6s", code).replace(' ', '0');
//
//            UrlEntity entity = new UrlEntity();
//            entity.setShortCode(code);
//            entity.setOriginalUrl("");
//            entity.setCreatedAt(LocalDateTime.now());
//            entity.setExpiredAt(null);
//            entity.setUseYn("Y");
//            entity.setClickCount(0);
//            return entity;
//        };
//    }
//
//    @Bean
//    public ItemWriter<UrlEntity> urlWriter(DataSource dataSource) {
//        return new JdbcBatchItemWriterBuilder<UrlEntity>()
//                .dataSource(dataSource)
//                .beanMapped()
//                .sql("""
//                INSERT INTO url_entity
//                (short_code, original_url, created_at, expired_at, use_yn, click_count)
//                VALUES
//                (:shortCode, :originalUrl, :createdAt, :expiredAt, :useYn, :clickCount)
//                """)
//                .build();
//    }
//}
