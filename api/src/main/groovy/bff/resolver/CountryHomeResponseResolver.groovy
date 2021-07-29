package bff.resolver

import bff.model.CountryFlagSize
import bff.model.CountryHomeResponse
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class CountryHomeResponseResolver implements GraphQLResolver<CountryHomeResponse> {
    @Autowired
    ImageService imageService

    String flag(CountryHomeResponse item, CountryFlagSize size) {
        return imageService.url(item.flag, size)
    }

}