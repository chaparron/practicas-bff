package bff.resolver

import bff.model.AdBanner
import bff.model.AdBannerImageSize
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class AdBannerResolver implements GraphQLResolver<AdBanner> {

    @Autowired
    ImageService imageService

    String desktop(AdBanner banner, AdBannerImageSize size) {
        imageService.url(banner.desktop, size)
    }

    String mobile(AdBanner banner, AdBannerImageSize size) {
        imageService.url(banner.mobile, size)
    }

    String link(AdBanner banner) {
        banner.link.orElse(null)
    }

}