package bff.resolver

import bff.model.SupportedPaymentProvider
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class SupportedPaymentProviderResolver implements GraphQLResolver<SupportedPaymentProvider> {

    String title(SupportedPaymentProvider supportedPaymentProvider, String languageTag) {
        // TODO: Use real implementation with MessageSource
        supportedPaymentProvider.title
    }

    String description(SupportedPaymentProvider supportedPaymentProvider, String languageTag) {
        // TODO: Use real implementation with MessageSource
        supportedPaymentProvider.description
    }
}
