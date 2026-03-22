package com.mybank.notification.domain.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class Notification {
    private String type;
    private String recipient;
    private String subject;
    private String message;
    private UUID transactionId;
    private BigDecimal amount;
}
