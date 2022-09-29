package bff.resolver

import bff.model.Badge
import bff.model.BadgeImageSize
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

import static java.util.Optional.ofNullable

@Component
@Slf4j
class BadgeResolver implements GraphQLResolver<Badge> {

    @Autowired
    ImageService imageService

    String image(Badge badge, BadgeImageSize size) {
        ofNullable(size).map { imageService.url(badge.image, it) }.orElse(badge.image)
    }

    String tooltip(Badge badge, LanguageTag languageTag) {
        badge.tooltip.map { it.call(languageTag) }.orElse(null)
    }

}