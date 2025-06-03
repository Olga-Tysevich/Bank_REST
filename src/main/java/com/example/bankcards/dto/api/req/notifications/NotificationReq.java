package com.example.bankcards.dto.api.req.notifications;

public interface NotificationReq {

    static <N extends NotificationReq> N castToConcreteNotification(NotificationReq notificationReq, Class<N> nClass) {
        if (notificationReq.getClass().equals(nClass)) {
            return nClass.cast(notificationReq);
        }

        throw new IllegalArgumentException("Notification is not of type " + nClass.getName());
    }
}
