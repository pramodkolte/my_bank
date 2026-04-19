package com.mybank.audit.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.util.concurrent.Executors;

@Configuration
public class SqsConfig {

    /**
     * By defining this bean, Spring Boot 3.2+ will automatically use it for
     * SQS listeners, @Async methods, and other task-related components.
     * Since virtual threads are enabled, this executor will use them for all tasks.
     */
    @Bean
    public AsyncTaskExecutor taskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
