package com.mybank.notification.application.port.in;

import java.util.Map;

public interface NotificationUseCase {
    void processTransactionCompleted(Map<String, Object> transactionPayload);
}
