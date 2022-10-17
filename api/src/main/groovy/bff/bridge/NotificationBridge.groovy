package bff.bridge

import bff.model.AccessTokenInput
import bff.model.GetMyNotificationsInput
import bff.model.NotificationResult
import bff.model.PaginatedNotificationResult
import bff.model.ReadNotificationInput
import bff.model.UnreadNotificationsInput
import bff.model.UnreadNotificationsResult
import bff.model.Void

interface NotificationBridge {
    PaginatedNotificationResult getAllMyNotifications(GetMyNotificationsInput input)
    NotificationResult readNotification(ReadNotificationInput input)
    UnreadNotificationsResult unreadNotifications(UnreadNotificationsInput input)
    Void readAllNotification(AccessTokenInput input)
}
