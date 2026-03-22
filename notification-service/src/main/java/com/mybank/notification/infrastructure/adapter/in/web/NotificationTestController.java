package com.mybank.notification.infrastructure.adapter.in.web;

import com.mybank.notification.application.port.in.NotificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationTestController {
    
    private final NotificationUseCase notificationUseCase;

    @PostMapping("/test-trigger")
    public void trigger(@RequestBody Map<String, Object> payload) {
        notificationUseCase.processTransactionCompleted(payload);
    }
}
