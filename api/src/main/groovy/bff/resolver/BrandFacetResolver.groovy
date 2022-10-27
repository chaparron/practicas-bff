package bff.resolver


import bff.model.BrandFacet
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
@Slf4j
class BrandFacetResolver implements GraphQLResolver<BrandFacet> {

    String label(BrandFacet facet, LanguageTag languageTag) {
        facet.label.call(languageTag)
    }

}