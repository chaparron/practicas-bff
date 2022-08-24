package bff.resolver

import bff.model.SimpleTextButton
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class SimpleTextButtonResolver implements GraphQLResolver<SimpleTextButton> {

    @Autowired
    MessageSource messageSource

    CompletableFuture<String> text(SimpleTextButton button, String languageTag) {
        Mono.just(messageSource.getMessage(
                "button.$button.textKey",
                null,
                "button.key",
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}
