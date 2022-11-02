package bff.resolver

import bff.model.MessageBox
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class MessageBoxResolver implements GraphQLResolver<MessageBox> {

    private MessageSource messageSource

    MessageBoxResolver(MessageSource messageSource) {
        this.messageSource = messageSource
    }

    CompletableFuture<String> title(MessageBox box, String languageTag) {
        textOrWarningMessage(box.titleKey, languageTag)
    }

    CompletableFuture<String> description(MessageBox box, String languageTag) {
        textOrWarningMessage(box.descriptionKey, languageTag)
    }

    private CompletableFuture<String> textOrWarningMessage(String key, String languageTag) {
        if (key != null) {
            Mono.just(messageSource.getMessage(key, null, "!!$key!!", Locale.forLanguageTag(languageTag))).toFuture()
        } else {
            Mono.<String> just("").toFuture()
        }
    }

}

