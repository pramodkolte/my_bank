package com.mybank.notification.infrastructure.adapter.out.web;

import com.mybank.notification.domain.model.Notification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

@FeignClient(name = "webhookClient", url = "http://placeholder")
public interface WebhookClient {
    @PostMapping
    void sendNotification(URI baseUrl, @RequestBody Notification notification);
}
