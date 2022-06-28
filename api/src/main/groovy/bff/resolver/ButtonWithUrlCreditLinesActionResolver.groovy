package bff.resolver


import bff.model.ButtonWithUrlCreditLinesAction
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
@Slf4j
class ButtonWithUrlCreditLinesActionResolver implements GraphQLResolver<ButtonWithUrlCreditLinesAction> {
    @Autowired
    MessageSource messageSource

    CompletableFuture<String> text(ButtonWithUrlCreditLinesAction button, String languageTag) {
        Mono.just(messageSource.getMessage(
                "bnpl.action.label",
                [button.provider.poweredBy.toUpperCase()].toArray(),
                button.provider.poweredBy.toUpperCase(),
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }

}