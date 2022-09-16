package bff.resolver

import bff.model.JPMorganMainPaymentProvider
import bff.model.JPMorganUPIPaymentProvider
import bff.model.PaymentProviderLogoSize
import bff.model.SupermoneyPaymentProvider
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
abstract class SupportedPaymentProviderResolver <T> {

    @Autowired
    MessageSource messageSource

    @Autowired
    ImageService imageService

    @Value('${upi.logo.id:}')
    String upiLogoId

    @Value('${dp.logo.id:}')
    String dpLogoId

    @Value('${bnpl.logo.id:}')
    String bnplLogoId

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

    abstract URI logo (T supportedPaymentProvider, PaymentProviderLogoSize size)

}

@Component
class JPMorganMainPaymentProviderResolver extends SupportedPaymentProviderResolver<JPMorganMainPaymentProvider> implements GraphQLResolver<JPMorganMainPaymentProvider> {
    JPMorganMainPaymentProviderResolver() {
        super()
    }

    @Override
    URI logo(JPMorganMainPaymentProvider supportedPaymentProvider, PaymentProviderLogoSize size) {
        imageService.url(dpLogoId, size).toURI()
    }
}

@Component
class SupermoneyPaymentProviderResolver extends SupportedPaymentProviderResolver<SupermoneyPaymentProvider> implements GraphQLResolver<SupermoneyPaymentProvider> {
    SupermoneyPaymentProviderResolver() {
        super()
    }

    @Override
    URI logo(SupermoneyPaymentProvider supportedPaymentProvider, PaymentProviderLogoSize size) {
        imageService.url(bnplLogoId, size).toURI()
    }
}

@Component
class JPMorganUPIPaymentProviderResolver extends SupportedPaymentProviderResolver<JPMorganUPIPaymentProvider> implements GraphQLResolver<JPMorganUPIPaymentProvider> {
    JPMorganUPIPaymentProviderResolver() {
        super()
    }

    @Override
    URI logo(JPMorganUPIPaymentProvider supportedPaymentProvider, PaymentProviderLogoSize size) {
        imageService.url(upiLogoId, size).toURI()
    }
}
