package com.mybank.notification.infrastructure.adapter.out.web;

import com.mybank.notification.domain.model.Notification;
import com.mybank.notification.domain.port.out.NotificationDispatcherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookNotificationAdapter implements NotificationDispatcherPort {

    private final WebhookClient webhookClient;

    @Value("${notification.webhook.url}")
    private String webhookUrl;

    @Override
    public void dispatch(Notification notification) {
        log.info("Dispatching notification payload to webhook {}: {}", webhookUrl, notification);
        try {
            webhookClient.sendNotification(URI.create(webhookUrl), notification);
            log.info("Successfully dispatched notification.");
        } catch (Exception e) {
            log.error("Failed to send notification to webhook: {}", e.getMessage());
        }
    }
}
