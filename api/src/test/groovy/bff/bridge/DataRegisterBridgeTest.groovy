package bff.bridge

import bff.bridge.sqs.DataRegisterBridgeImpl
import com.amazonaws.services.sqs.AmazonSQS
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.class)
class DataRegisterBridgeTest {

    @Mock
    AmazonSQS amazonSQS

    @InjectMocks
    DataRegisterBridgeImpl dataRegisterBridge = new DataRegisterBridgeImpl()

    @Before
    void init() {
        dataRegisterBridge.queueUrl = "http://localhost:3000/"
    }

    @Test
    void sendMessage() {
        def response = dataRegisterBridge.sendMessage("1", ["1", "2"])
        Assert.assertTrue(response)
    }
}
