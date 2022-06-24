package bff.resolver

import bff.model.CreditLineProvider
import bff.model.CreditLines
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

import static bff.model.BnplCreditLineQuery.BNPL_PROPERTY_PREFIX

@Component
@Slf4j
class CreditLinesResolver implements GraphQLResolver<CreditLines> {
    @Autowired
    MessageSource messageSource

    CompletableFuture<String> providerLabel(CreditLines creditLines, String languageTag) {
        def key = BNPL_PROPERTY_PREFIX + "provider." + creditLines.provider.provider.name()
        Mono.just(messageSource.getMessage(key, null, key, Locale.forLanguageTag(languageTag))).toFuture()
    }
}

@Component
@Slf4j
class CreditLineProviderResolver implements GraphQLResolver<CreditLineProvider> {
    @Autowired
    MessageSource messageSource

    CompletableFuture<String> poweredByLabel(CreditLineProvider creditLineProvider, String languageTag) {
        def key = BNPL_PROPERTY_PREFIX + "provider." + creditLineProvider.provider.name()
        Mono.just(messageSource.getMessage(key, null, key, Locale.forLanguageTag(languageTag))).toFuture()
    }
}