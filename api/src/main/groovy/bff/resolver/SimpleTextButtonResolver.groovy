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
        def key = "button.$button.textKey"
        Mono.just(messageSource.getMessage(key, null, "!!$key!!", Locale.forLanguageTag(languageTag))).toFuture()
    }

    CompletableFuture<String> message(SimpleTextButton button, String languageTag) {
        if (button.messageKey == null) {
            return Mono.<String> justOrEmpty(null).toFuture()
        } else {
            def key = "button.message.$button.messageKey"
            return Mono.just(messageSource.getMessage(key, null, "!!$key!!", Locale.forLanguageTag(languageTag))).toFuture()
        }
    }

}
