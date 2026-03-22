package com.mybank.transaction.infrastructure.adapter.out.messaging;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
    "resilience4j.circuitbreaker.instances.kafkaPublisher.slidingWindowSize=2",
    "resilience4j.circuitbreaker.instances.kafkaPublisher.minimumNumberOfCalls=2"
})
class KafkaCircuitBreakerTest {

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaTransactionPublisherAdapter publisherAdapter;

    @Test
    void shouldTriggerFallbackOnKafkaFailure() {
        // Simulate Kafka failure
        when(kafkaTemplate.send(anyString(), anyString(), any()))
            .thenThrow(new RuntimeException("Kafka broker down"));

        Transaction tx = Transaction.builder()
            .id(UUID.randomUUID())
            .senderAccountId(UUID.randomUUID())
            .receiverAccountId(UUID.randomUUID())
            .amount(BigDecimal.TEN)
            .type(TransactionType.TRANSFER)
            .status(TransactionStatus.INITIATED)
            .build();

        // The adapter method is wrapped in @CircuitBreaker. It should call fallback upon encountering Exception.
        assertThatThrownBy(() -> publisherAdapter.publishTransactionInitiatedEvent(tx))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Kafka broker is unreachable");
    }
}
