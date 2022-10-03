package bff.resolver

import bff.bridge.http.CountryGatewayBridgeImpl
import bff.model.Country
import bff.model.CountryFlagSize
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import wabi2b.sdk.regional.RegionalConfigSdk

@Component
@Slf4j
class CountryResolver implements GraphQLResolver<Country> {

    @Autowired
    ImageService imageService

    @Autowired
    CountryGatewayBridgeImpl countryGatewayBridgeImpl

    String flag(Country item, CountryFlagSize size) {
        return imageService.url(item.flag, size)
    }

}