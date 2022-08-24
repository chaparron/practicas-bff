package bff.resolver

import bff.model.JPMorganPaymentProvider
import bff.model.SupermoneyPaymentProvider
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class SupportedPaymentProviderResolver <T> {

    @Autowired
    MessageSource messageSource

    CompletableFuture<String> title(T supportedPaymentProvider, String languageTag) {
        Mono.just(messageSource.getMessage(
                "payment.provider.title.${supportedPaymentProvider.getClass().getSimpleName()}",
                null,
                "payment.provider.title",
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }

    CompletableFuture<String> description(T supportedPaymentProvider, String languageTag) {
        Mono.just(messageSource.getMessage(
                "payment.provider.description.${supportedPaymentProvider.getClass().getSimpleName()}",
                null,
                "payment.provider.description",
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }

    CompletableFuture<String> poweredByLabel(T supportedPaymentProvider, String languageTag) {
        Mono.just(messageSource.getMessage(
                "payment.provider.poweredByLabel.${supportedPaymentProvider.getClass().getSimpleName()}",
                null,
                "payment.provider.poweredByLabel",
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}

@Component
class JPMorganPaymentProviderResolver extends SupportedPaymentProviderResolver<JPMorganPaymentProvider> implements GraphQLResolver<JPMorganPaymentProvider> {
    JPMorganPaymentProviderResolver() {
        super()
    }
}

@Component
class SupermoneyPaymentProviderResolver extends SupportedPaymentProviderResolver<SupermoneyPaymentProvider> implements GraphQLResolver<SupermoneyPaymentProvider> {
    SupermoneyPaymentProviderResolver() {
        super()
    }
}
