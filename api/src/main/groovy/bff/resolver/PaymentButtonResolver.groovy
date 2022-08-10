package bff.resolver

import bff.model.PaymentButton
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class PaymentButtonResolver implements GraphQLResolver<PaymentButton> {

    @Autowired
    MessageSource messageSource

    CompletableFuture<String> text(PaymentButton paymentButton, String languageTag) {
        Mono.just(messageSource.getMessage(
                "supplierOrder.payment.button",
                null,
                "",
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}
