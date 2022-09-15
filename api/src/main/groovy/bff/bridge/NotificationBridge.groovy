package bff.bridge

import bff.model.GetMyNotificationsInput
import bff.model.NotificationResult
import bff.model.ReadNotificationInput

interface NotificationBridge {
    List<NotificationResult> getAllMyNotifications(GetMyNotificationsInput input)

    NotificationResult readNotification(ReadNotificationInput input)
}
