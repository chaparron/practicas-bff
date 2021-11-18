package bff.bridge.sqs

import bff.bridge.DataRegisterBridge
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.MessageAttributeValue
import com.amazonaws.services.sqs.model.SendMessageRequest
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

@Slf4j
class DataRegisterBridgeImpl implements DataRegisterBridge {

    @Autowired
    AmazonSQS amazonSQS

    @Value("data.register.queue.url:")
    String queueUrl

    @Override
    Boolean sendMessage(String googleSpreadsheetId, List<String> values) {
        try {
            final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>()
            messageAttributes.put("spreadsheet_id", new MessageAttributeValue()
                    .withDataType("String")
                    .withStringValue(googleSpreadsheetId))

            final SendMessageRequest sendMessageRequest = new SendMessageRequest()
            sendMessageRequest.withMessageBody(JsonOutput.toJson(values))
            sendMessageRequest.withQueueUrl(queueUrl)
            sendMessageRequest.withMessageAttributes(messageAttributes)
            amazonSQS.sendMessage(sendMessageRequest)
        } catch (Exception e) {
            log.error("Error sending message to SQS: ", e)
            return false
        }

        return true
    }
}
