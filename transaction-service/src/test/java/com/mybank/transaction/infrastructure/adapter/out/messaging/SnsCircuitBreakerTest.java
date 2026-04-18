package com.mybank.transaction.infrastructure.adapter.out.messaging;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;
import com.mybank.transaction.domain.port.out.AccountClientPort;
import com.mybank.transaction.domain.port.out.TransactionRepositoryPort;
import com.mybank.transaction.infrastructure.adapter.in.messaging.SqsSagaReplyListener;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = {
                "resilience4j.circuitbreaker.instances.snsPublisher.slidingWindowSize=2",
                "resilience4j.circuitbreaker.instances.snsPublisher.minimumNumberOfCalls=2"
})
class SnsCircuitBreakerTest {

        @SuppressWarnings("removal")
        @MockBean
        private SnsTemplate snsTemplate;

        // Mock infrastructure beans so the context loads without real connections
        @SuppressWarnings("removal")
        @MockBean
        private TransactionRepositoryPort transactionRepositoryPort;

        @SuppressWarnings("removal")
        @MockBean
        private AccountClientPort accountClientPort;

        @SuppressWarnings("removal")
        @MockBean
        private JwtDecoder jwtDecoder;

        // Prevents @SqsListener from resolving queue attributes against real AWS
        @SuppressWarnings("removal")
        @MockBean
        private SqsSagaReplyListener sqsSagaReplyListener;

        @Autowired
        private SnsTransactionPublisherAdapter publisherAdapter;

        @SuppressWarnings("null")
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

                // The adapter method is wrapped in @CircuitBreaker. It should call fallback
                // upon encountering Exception.
                assertThatThrownBy(() -> publisherAdapter.publishTransactionInitiatedEvent(tx))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("SNS is unreachable");
        }
}
