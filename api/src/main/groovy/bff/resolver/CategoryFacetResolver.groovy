package bff.resolver


import bff.model.CategoryFacet
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
@Slf4j
class CategoryFacetResolver implements GraphQLResolver<CategoryFacet> {

    String label(CategoryFacet facet, LanguageTag languageTag) {
        facet.label.call(languageTag)
    }

}