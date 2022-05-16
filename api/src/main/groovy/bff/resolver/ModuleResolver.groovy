package bff.resolver

import bff.bridge.sdk.Cms
import bff.model.*
import bff.service.ImageService
import bff.support.ExecutorService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

import java.util.concurrent.CompletableFuture

import static java.util.Optional.ofNullable

@Component
class ModuleResolver implements GraphQLResolver<Module> {

    @Autowired
    Cms cms
    @Autowired
    ExecutorService executor
    @Autowired
    ImageService imageService

    String title(Module module, LanguageTag languageTag) {
        module.title.map { it.getOrDefault(languageTag) }.orElse(null)
    }

    String titleIcon(Module module, TitleIconSize size) {
        module.titleIcon.map { imageService.url(it, size) }.orElse(null)
    }

    String link(Module module) {
        module.link.orElse(null)
    }

    TimestampOutput expiration(Module module) {
        module.expiration.orElse(null)
    }

    CompletableFuture<List<Piece>> pieces(Module module, ContextInput context, Integer size) {
        executor.doAsync {
            cms.build(module, context, ofNullable(size))
        }
    }

}
