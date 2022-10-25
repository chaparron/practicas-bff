package bff.resolver


import bff.model.CategorySlice
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
@Slf4j
class CategorySliceResolver implements GraphQLResolver<CategorySlice> {

    String name(CategorySlice category, LanguageTag languageTag) {
        category.name.call(languageTag)
    }

}