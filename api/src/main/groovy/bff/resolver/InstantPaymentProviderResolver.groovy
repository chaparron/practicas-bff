package bff.resolver

import bff.model.InstantPaymentProvider
import bff.service.PaymentProviderTranslationService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class InstantPaymentProviderResolver implements GraphQLResolver<InstantPaymentProvider> {

    @Autowired
    private PaymentProviderTranslationService paymentProviderTranslationService

    CompletableFuture<String> poweredByLabel(InstantPaymentProvider instantPaymentProvider, String languageTag) {
        Mono.just(
                paymentProviderTranslationService.poweredByLabel(instantPaymentProvider, languageTag)
        ).toFuture()
    }
}
