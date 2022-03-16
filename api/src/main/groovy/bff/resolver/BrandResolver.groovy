package bff.resolver


import bff.model.BannerLogoSize
import bff.model.Brand
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

@Component
@Slf4j
class BrandResolver implements GraphQLResolver<Brand> {

    @Autowired
    ImageService imageService

    String logo(Brand brand, BannerLogoSize size) {
        ofNullable(brand.logo)
                .map { logo ->
                    ofNullable(size).map { imageService.url(logo, it) }.orElse(logo)
                }
                .orElse(null)
    }

}