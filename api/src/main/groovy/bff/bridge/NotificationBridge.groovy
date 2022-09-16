package bff.bridge

import bff.model.GetMyNotificationsInput
import bff.model.NotificationResult
import bff.model.PaginatedNotificationResult
import bff.model.ReadNotificationInput

interface NotificationBridge {
    PaginatedNotificationResult getAllMyNotifications(GetMyNotificationsInput input)

    NotificationResult readNotification(ReadNotificationInput input)
}
