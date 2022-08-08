package bff.resolver

import bff.model.CreditLineProvider

import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class CreditLineProviderResolver implements GraphQLResolver<CreditLineProvider> {

    @Autowired
    private MessageSource messageSource

    CompletableFuture<String> poweredByLabel(CreditLineProvider creditLineProvider, String languageTag) {
        Mono.just(messageSource.getMessage(
                "bnpl.creditProvider.poweredBy.label",
                [creditLineProvider.provider.poweredBy].toArray(),
                creditLineProvider.provider.poweredBy,
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}
