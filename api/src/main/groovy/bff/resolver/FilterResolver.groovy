package bff.resolver

import bff.model.Filter
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class FilterResolver implements GraphQLResolver<Filter> {

    String value(Filter item, LanguageTag languageTeg) {
        item.value?.call(languageTeg)
    }

}
