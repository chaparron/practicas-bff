package bff.model

import bff.bridge.sdk.Cms
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class CmsQuery implements GraphQLQueryResolver {

    @Autowired
    Cms cms

    List<Module> homeModules(HomeInput input) {
        cms.find(input)
    }

    List<Module> listingModules(ListingInput input) {
        cms.find(input)
    }

    List<Module> landingModules(LandingInput input) {
        cms.find(input)
    }

}



