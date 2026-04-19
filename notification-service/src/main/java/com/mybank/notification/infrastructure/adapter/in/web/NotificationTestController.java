package com.mybank.notification.infrastructure.adapter.in.web;

import com.mybank.notification.application.port.in.NotificationUseCase;
import com.mybank.notification.infrastructure.adapter.in.web.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationTestController {
    
    private final NotificationUseCase notificationUseCase;

    @PostMapping("/test-trigger")
    public ResponseEntity<ApiResponse<Void>> trigger(@RequestBody Map<String, Object> payload) {
        notificationUseCase.processTransactionCompleted(payload);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Notification test triggered")
                .build();
        return ResponseEntity.ok(response);
    }
}
