package bff.resolver


import bff.model.SupplierFacet
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
@Slf4j
class SupplierFacetResolver implements GraphQLResolver<SupplierFacet> {

    String label(SupplierFacet facet, LanguageTag languageTag) {
        facet.label.call(languageTag)
    }

}