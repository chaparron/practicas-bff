package bff.resolver

import bff.model.PaymentMode
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class PaymentModeResolver implements GraphQLResolver<PaymentMode> {

    String text(PaymentMode paymentMode, String languageTag) {
        paymentMode.paymentType.name()
    }
}
