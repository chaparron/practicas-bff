package bff.bridge.http

import bff.bridge.NotificationBridge
import bff.model.GetMyNotificationsInput
import bff.model.NotificationMessage
import bff.model.NotificationParams
import bff.model.NotificationResult
import bff.model.ReadNotificationInput
import com.wabi2b.notifications.common.NotificationResponse
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
    List<NotificationResult> getAllMyNotifications(GetMyNotificationsInput input) {
        List<NotificationResponse> notifications = client.getAllNotifications(input.getPageSize(), input.getStartKey(), input.getToken())
        return notifications?.collect(){
            mapNotification(it)
        }
    }

    @Override
    NotificationResult readNotification(ReadNotificationInput input) {
        return mapNotification(client.readNotification(input.getNotificationId(), input.getToken()))
    }

    private NotificationResult mapNotification(com.wabi2b.notifications.common.NotificationResponse notification) {
        return new NotificationResult(
                id: notification.getId(),
                url: notification.getUrl(),
                creationDate: notification.getCreationDate(),
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
                logo: "https://www.wabi2b.com/assets/images/wabi2b/logo.svg"
        )
    }

}
