package bff.resolver


import bff.model.BannerLogoSize
import bff.model.BrandSlice
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

import static java.util.Optional.ofNullable

@Component
@Slf4j
class BrandSliceResolver implements GraphQLResolver<BrandSlice> {

    @Autowired
    ImageService imageService

    String name(BrandSlice brand, LanguageTag languageTag) {
        brand.name.call(languageTag)
    }

    String logo(BrandSlice brand, BannerLogoSize size) {
        ofNullable(brand.logo)
                .map { logo ->
                    ofNullable(size).map { imageService.url(logo, it) }.orElse(logo)
                }
                .orElse(null)
    }

}