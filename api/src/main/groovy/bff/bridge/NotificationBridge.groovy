package bff.bridge

import bff.model.GetMyNotificationsInput
import bff.model.NotificationResult
import bff.model.PaginatedNotificationResult
import bff.model.ReadNotificationInput
import bff.model.UnreadNotificationsInput
import bff.model.UnreadNotificationsResult

interface NotificationBridge {
    PaginatedNotificationResult getAllMyNotifications(GetMyNotificationsInput input)
    NotificationResult readNotification(ReadNotificationInput input)
    UnreadNotificationsResult unreadNotifications(UnreadNotificationsInput input)
}
