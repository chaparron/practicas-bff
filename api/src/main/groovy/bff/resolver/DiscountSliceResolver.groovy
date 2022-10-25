package bff.resolver

import bff.model.DiscountSlice
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
@Slf4j
class DiscountSliceResolver implements GraphQLResolver<DiscountSlice> {

    String label(DiscountSlice discount, LanguageTag languageTag) {
        discount.label.call(languageTag)
    }

}