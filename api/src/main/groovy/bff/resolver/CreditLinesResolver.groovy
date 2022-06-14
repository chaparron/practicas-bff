package bff.resolver


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
        def key = BNPL_PROPERTY_PREFIX + "provider." + creditLines.provider.name()
        Mono.just(messageSource.getMessage(key, null, key, Locale.forLanguageTag(languageTag))).toFuture()
    }
}