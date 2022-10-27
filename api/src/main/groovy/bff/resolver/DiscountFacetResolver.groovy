package bff.resolver


import bff.model.DiscountFacet
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
@Slf4j
class DiscountFacetResolver implements GraphQLResolver<DiscountFacet> {

    String label(DiscountFacet facet, LanguageTag languageTag) {
        facet.label.call(languageTag)
    }

}