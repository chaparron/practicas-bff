package bff.resolver

import bff.model.CreditLineProvider
import bff.service.PaymentProviderTranslationService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class CreditLineProviderResolver implements GraphQLResolver<CreditLineProvider> {

    @Autowired
    private PaymentProviderTranslationService paymentProviderTranslationService

    CompletableFuture<String> poweredByLabel(CreditLineProvider creditLineProvider, String languageTag) {
        Mono.just(
                paymentProviderTranslationService.poweredByLabel(creditLineProvider, languageTag)
        ).toFuture()
    }
}
