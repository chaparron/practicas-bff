package bff.resolver

import bff.model.Country
import bff.model.CountryFlagSize
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class CountryResolver implements GraphQLResolver<Country> {
    @Autowired
    ImageService imageService

    String flag(Country item, CountryFlagSize size) {
        return imageService.url(item.flag, size)
    }

}