package bff.bridge.sdk

import bff.JwtToken
import bff.model.MoneyInput
import bff.model.RequestForExternalPayment
import bff.model.RequestForExternalPaymentFailed
import bff.model.CreateExternalPaymentFailureReason
import bff.model.RequestForExternalPaymentInput
import bff.model.RequestForExternalPaymentResult
import bff.model.ExternalPaymentInformation
import bff.model.ExternalPaymentsInput
import bff.model.ExternalPaymentsResult
import bff.model.Money
import bff.model.TimestampOutput
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import wabi2b.payments.common.model.dto.ExternalPayment
import wabi2b.payments.common.model.response.UserWalletResponse
import wabi2b.payments.sdk.client.WabiPaymentSdkClient

@Slf4j
class ExternalPayments {

    private WabiPaymentSdkClient sdk

    @Value('${wabipay.url:https://www-qa.wabipay.com/}')
    String wabipayUrl

    ExternalPaymentsResult getMyExternalPayments(ExternalPaymentsInput i) {
        def payments = sdk.getUserExternalPayments(i.scrollInput.size, i.scrollInput.scroll, i.accessToken).block()
        new ExternalPaymentsResult(scroll: payments.getPageInfo().endCursor?.value,
                externalPayments: payments.getEdges().collect {
                    from(it.node)
                })
    }

    RequestForExternalPaymentResult generateExternalPaymentUrl(RequestForExternalPaymentInput input) {
        if (!isFromAllowedCountry(input.accessToken)) throw new UnsupportedOperationException("User country is not allowed")
        if (sdk.existsUser(input.target, input.accessToken).block()) {
            UserWalletResponse myWallet = sdk.getMyWallet(input.accessToken).block()
            def encoded = Base64.getEncoder().encodeToString(
                    new ObjectMapper().writeValueAsBytes(
                            new ExternalPaymentsUrl(input.amount, myWallet.walletId,
                                    input.target, JwtToken.userIdFromToken(input.accessToken)
                            ))).replaceAll("=", "")
            return new RequestForExternalPayment(url: "${wabipayUrl}payments/checkouts/$encoded?at=${input.accessToken}")
        }
        return new RequestForExternalPaymentFailed(
                reason: CreateExternalPaymentFailureReason.TARGET_WALLET_NOT_FOUND)
    }

    private static Boolean isFromAllowedCountry(String accessToken) {
        JwtToken.countryFromString(accessToken) == "my"
    }

    static ExternalPaymentInformation from(ExternalPayment input) {
        new ExternalPaymentInformation(
                amount: new Money(currency: input.money.currency, amount: input.money.amount),
                receiver: input.receiver,
                created: new TimestampOutput(input.created.toString())
        )
    }

    class ExternalPaymentsUrl {
        @JsonProperty("a")
        Double amount
        @JsonProperty("cur")
        String currency
        @JsonProperty("pay")
        String payer
        @JsonProperty("rec")
        String receiver
        @JsonProperty("eId")
        String externalId

        ExternalPaymentsUrl(MoneyInput money, String payer, String receiver, String externalId) {
            this.amount = money.amount
            this.currency = money.currency
            this.payer = payer
            this.receiver = receiver
            this.externalId = externalId
        }
    }

}
