package bff.resolver


import bff.model.LoanPaymentFailed
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class LoanPaymentFailedResolver implements GraphQLResolver<LoanPaymentFailed> {
    @Autowired
    private MessageSource messageSource

    CompletableFuture<String> text(LoanPaymentFailed failure, String languageTag) {
        def key = "bnpl.loan.payment.error.${failure.reason.name()}"
        Mono.just(messageSource.getMessage(
                key,
                null,
                key,
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}
