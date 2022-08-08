package bff.resolver

import bff.model.SupportedPaymentProvider
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class SupportedPaymentProviderResolver implements GraphQLResolver<SupportedPaymentProvider> {

    @Autowired
    MessageSource messageSource

    CompletableFuture<String> title(SupportedPaymentProvider supportedPaymentProvider, String languageTag) {
        Mono.just(messageSource.getMessage(
                "payment.provider.title.${supportedPaymentProvider.configuration.code.name()}",
                null,
                supportedPaymentProvider.configuration.code.name(),
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }

    CompletableFuture<String> description(SupportedPaymentProvider supportedPaymentProvider, String languageTag) {
        Mono.just(messageSource.getMessage(
                "payment.provider.description.${supportedPaymentProvider.configuration.code.name()}",
                null,
                supportedPaymentProvider.configuration.code.name(),
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }

    CompletableFuture<String> poweredByLabel(SupportedPaymentProvider supportedPaymentProvider, String languageTag) {
        Mono.just(messageSource.getMessage(
                "payment.provider.poweredByLabel.${supportedPaymentProvider.configuration.code.name()}",
                null,
                supportedPaymentProvider.configuration.code.name(),
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}
