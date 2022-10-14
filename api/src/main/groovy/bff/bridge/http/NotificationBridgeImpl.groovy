package bff.bridge.http

import bff.bridge.NotificationBridge
import bff.model.GetMyNotificationsInput
import bff.model.NotificationMessage
import bff.model.NotificationParams
import bff.model.NotificationResult
import bff.model.PaginatedNotificationResult
import bff.model.ReadNotificationInput
import bff.model.TimestampOutput
import bff.model.UnreadNotificationsInput
import bff.model.UnreadNotificationsResult
import com.wabi2b.notifications.common.PaginatedNotificationResponse
import com.wabi2b.notifications.sdk.NotificationClient
import com.wabi2b.notifications.sdk.NotificationHttpClient
import groovy.util.logging.Slf4j

@Slf4j
class NotificationBridgeImpl implements NotificationBridge {

    private final NotificationClient client

    NotificationBridgeImpl(String root) {
        client = new NotificationHttpClient(root)
    }

    @Override
    PaginatedNotificationResult getAllMyNotifications(GetMyNotificationsInput input) {
        PaginatedNotificationResponse notifications = client.getAllNotifications(input.getPageSize(), input.getCursor(), input.getAccessToken())
        List<NotificationResult> resultList = notifications.content?.collect() {
            mapNotification(it)
        }
        return new PaginatedNotificationResult(cursor: notifications.cursor, content: resultList)
    }

    @Override
    NotificationResult readNotification(ReadNotificationInput input) {
        return mapNotification(client.readNotification(input.getNotificationId(), input.getAccessToken()))
    }

    @Override
    UnreadNotificationsResult unreadNotifications(UnreadNotificationsInput input) {
        return new UnreadNotificationsResult(unread: client.unreadNotification(input.getAccessToken()).unread)
    }

    private NotificationResult mapNotification(com.wabi2b.notifications.common.NotificationResponse notification) {
        return new NotificationResult(
                id: notification.getId(),
                url: notification.getUrl(),
                creationDate: new TimestampOutput(new Date(notification.getCreationDate()).toInstant().toString()),
                isRead: notification.isRead(),
                templateId: notification.getTemplateId(),
                params: toMap(notification.getParams()),
                message: mapNotificationMessage(notification.getMessage())
        )
    }

    private List<NotificationParams> toMap(Map<String, String> params) {
        List<NotificationParams> list = new ArrayList<NotificationParams>()

        params.forEach{k, v ->
            NotificationParams param = new NotificationParams(key: k, value: v)
            list.add(param)
        }
        return list
    }

    private NotificationMessage mapNotificationMessage(com.wabi2b.notifications.common.Message message) {
        return new NotificationMessage(
                body: message.getBody(),
                title: message.getTitle(),
                logo: message.getLogo()
        )
    }

}
