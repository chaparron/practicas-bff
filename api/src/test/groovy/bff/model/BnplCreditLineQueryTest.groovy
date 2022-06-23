package bff.model

import bff.JwtToken
import bnpl.sdk.BnPlSdk
import bnpl.sdk.model.CreditLineResponse
import bnpl.sdk.model.MoneyResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import reactor.core.publisher.Mono

import static bff.TestExtensions.validAccessToken
import static java.math.BigDecimal.TEN

@RunWith(MockitoJUnitRunner.class)
class BnplCreditLineQueryTest {
    @Mock
    BnPlSdk bnPlSdk
    @InjectMocks
    BnplCreditLineQuery sut

    @Test
    void 'when getCreditLines should request it to the sdk'() {
        def input  = new CreditLinesRequestInput(accessToken: validAccessToken())
        String customerId = JwtToken.userIdFromToken(input.accessToken)
        def sdkResponse = new CreditLineResponse(
                customerId,
                new MoneyResponse("INR", TEN),
                new MoneyResponse("INR", TEN),
                null
        )

        Mockito.when(bnPlSdk.fetchBalance(customerId, input.accessToken)).thenReturn(Mono.just(sdkResponse))

        assert sut.getCreditLines(input).get() == CreditLines.fromSdk(sdkResponse)
    }
}
