package com.mybank.notification.domain.port.out;

import com.mybank.notification.domain.model.Notification;

public interface NotificationDispatcherPort {
    void dispatch(Notification notification);
}
