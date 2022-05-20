package bff.resolver

import bff.model.RequestForExternalPaymentFailed
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class RequestForExternalPaymentFailedResolver implements GraphQLResolver<RequestForExternalPaymentFailed> {

    @Autowired
    MessageSource messageSource

    String text(RequestForExternalPaymentFailed failed, String languageTag) {
        return messageSource.getMessage("externalPayment.wallet_not_found", null, Locale.forLanguageTag(languageTag))
    }
}
