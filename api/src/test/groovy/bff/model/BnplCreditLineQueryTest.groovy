package bff.model

import bff.JwtToken
import bff.bridge.BnplBridge
import bnpl.sdk.model.CreditLineResponse
import bnpl.sdk.model.MoneyResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

import static bff.TestExtensions.validAccessToken
import static java.math.BigDecimal.TEN

@RunWith(MockitoJUnitRunner.class)
class BnplCreditLineQueryTest {
    @Mock
    BnplBridge bnplBridge

    @InjectMocks
    BnplCreditLineQuery sut

    @Test
    void 'when getCreditLines should request it to the sdk'() {
        def input = new CreditLinesRequestInput(accessToken: validAccessToken())
        def customerUserId = JwtToken.userIdFromToken(input.accessToken).toLong()
        def sdkResponse = new CreditLineResponse(
                customerUserId,
                new MoneyResponse("INR", TEN),
                new MoneyResponse("INR", TEN),
                null
        )
        def creditLines = CreditLines.fromSdk(sdkResponse)

        Mockito.when(bnplBridge.userBalance(input.accessToken)).thenReturn(creditLines)

        assert sut.getCreditLines(input).get() == creditLines
    }
}
