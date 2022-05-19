package bff.bridge.sdk

import bff.model.ExternalPaymentInformation
import bff.model.ExternalPaymentsInput
import bff.model.ExternalPaymentsResult
import bff.model.Money
import bff.model.TimestampOutput
import groovy.util.logging.Slf4j
import wabi2b.payments.common.model.dto.ExternalPayment
import wabi2b.payments.sdk.client.WabiPaymentSdkClient

@Slf4j
class ExternalPayments {

    private WabiPaymentSdkClient sdk

    ExternalPaymentsResult getMyExternalPayments(ExternalPaymentsInput i) {
        def payments = sdk.getUserExternalPayments(i.scrollInput.size, i.scrollInput.scroll, i.accessToken).block()
        new ExternalPaymentsResult(scroll:payments.getPageInfo().endCursor?.value,
                externalPayments: payments.getEdges().collect {
                    from(it.node)
                })
    }

    static ExternalPaymentInformation from(ExternalPayment input) {
        new ExternalPaymentInformation(
                amount: new Money(currency: input.money.currency, amount: input.money.amount),
                receiver: input.receiver,
                created: new TimestampOutput(input.created.toString())
        )
    }

}
