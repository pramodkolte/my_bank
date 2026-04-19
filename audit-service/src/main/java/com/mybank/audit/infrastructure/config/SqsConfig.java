package com.mybank.audit.infrastructure.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.concurrent.Executors;

@Configuration
public class SqsConfig {

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
        return SqsMessageListenerContainerFactory
                .builder()
                .configure(options -> options
                        .componentsConfiguration(components -> components
                                .taskExecutor(virtualThreadProcessorExecutor())))
                .sqsAsyncClient(sqsAsyncClient)
                .build();
    }

    @Bean
    public AsyncTaskExecutor virtualThreadProcessorExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options.acknowledgementMode(io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode.ON_SUCCESS))
                .build();
    }
}
