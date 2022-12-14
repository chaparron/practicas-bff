package bff.resolver

import bff.model.FeatureFacet
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
@Slf4j
class FeatureFacetResolver implements GraphQLResolver<FeatureFacet> {

    String label(FeatureFacet feature, LanguageTag languageTag) {
        feature.label.call(languageTag)
    }

}