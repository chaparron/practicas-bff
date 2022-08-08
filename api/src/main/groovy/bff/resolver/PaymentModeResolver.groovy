package bff.resolver

import bff.model.PaymentMode
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class PaymentModeResolver implements GraphQLResolver<PaymentMode> {

    @Autowired
    MessageSource messageSource

    CompletableFuture<String> text(PaymentMode paymentMode, String languageTag) {
        Mono.just(messageSource.getMessage(
                "payment.provider.mode.${paymentMode.paymentType.name()}",
                null,
                paymentMode.paymentType.name(),
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}
