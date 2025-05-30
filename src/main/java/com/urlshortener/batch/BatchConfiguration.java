package com.urlshortener.batch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import com.urlshortener.entity.UrlEntity;
import com.urlshortener.repository.UrlRepository;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

    private static final long TOTAL_CODES = (long) Math.pow(36, 6);
    private static final int  BATCH_SIZE  = 10_000;

    private final UrlRepository urlRepository;
    private final DataSource    dataSource;
    private final EntityManagerFactory emf;

    // 카운터와 시작 시간
    private final AtomicLong insertedCount = new AtomicLong();
    private long startTime;

    public BatchConfiguration(UrlRepository urlRepository,
                              DataSource dataSource,
                              EntityManagerFactory emf) {
        this.urlRepository = urlRepository;
        this.dataSource    = dataSource;
        this.emf           = emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public JobRepository jobRepository(PlatformTransactionManager txManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(txManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public ItemReader<UrlEntity> urlItemReader() {
        return new AbstractItemStreamItemReader<>() {
            private long seq = 0;

            @Override
            public UrlEntity read() {
                if (seq == 0) {
                    startTime = System.currentTimeMillis(); // 첫 호출 시점 기록
                }
                if (seq >= TOTAL_CODES) {
                    return null;
                }
                String code = Long.toString(seq, 36);
                code = String.format("%6s", code).replace(' ', '0');
                seq++;
                return new UrlEntity(code, "", LocalDateTime.now(), null, 0);
            }
        };
    }

    @Bean
    public ItemWriter<UrlEntity> urlItemWriter() {
        JpaItemWriter<UrlEntity> delegate = new JpaItemWriter<>();
        delegate.setEntityManagerFactory(emf);

        return items -> {
            delegate.write(items);
            long inserted = insertedCount.addAndGet(items.size());
            long elapsed   = System.currentTimeMillis() - startTime;
            long remaining = TOTAL_CODES - inserted;
            double avgMs   = (double) elapsed / inserted;
            long etaMs     = (long) (avgMs * remaining);

            logger.info("전체 코드: {}건, 저장된 코드: {}건, 남은 코드: {}건, 경과: {}ms, 예상 잔여: {}ms",
                    TOTAL_CODES, inserted, remaining, elapsed, etaMs);
        };
    }

    @Bean
    public Step generateAllCodesStep(
            JobRepository jobRepository,
            PlatformTransactionManager txManager
    ) {
        return new StepBuilder("generateAllCodesStep", jobRepository)
                .<UrlEntity, UrlEntity>chunk(BATCH_SIZE, txManager)
                .reader(urlItemReader())
                .writer(urlItemWriter())
                .build();
    }

    @Bean
    public Job generateAllCodesJob(
            JobRepository jobRepository,
            Step generateAllCodesStep
    ) {
        return new JobBuilder("generateAllCodesJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(generateAllCodesStep)
                .build();
    }
}
