package com.mybank.transaction.infrastructure.adapter.out.messaging;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
    "resilience4j.circuitbreaker.instances.snsPublisher.slidingWindowSize=2",
    "resilience4j.circuitbreaker.instances.snsPublisher.minimumNumberOfCalls=2"
})
class SnsCircuitBreakerTest {

    @MockBean
    private SnsTemplate snsTemplate;

    @Autowired
    private SnsTransactionPublisherAdapter publisherAdapter;

    @Test
    void shouldTriggerFallbackOnSnsFailure() {
        // Simulate SNS failure
        doThrow(new RuntimeException("SNS unreachable"))
            .when(snsTemplate).convertAndSend(anyString(), any(Object.class));

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
            .hasMessageContaining("SNS is unreachable");
    }
}
