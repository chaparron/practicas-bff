package bff.resolver

import bff.model.DigitalPaymentFailed
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class DigitalPaymentFailedResolver implements GraphQLResolver<DigitalPaymentFailed> {
    @Autowired
    private MessageSource messageSource

    CompletableFuture<String> text(DigitalPaymentFailed failure, String languageTag) {
        def key = "digital.payment.error.label.${failure.reason.name()}"
        Mono.just(messageSource.getMessage(
                key,
                null,
                key,
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}