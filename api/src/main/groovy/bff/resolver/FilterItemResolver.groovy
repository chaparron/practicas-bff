package bff.resolver

import bff.model.FilterItem
import bff.model.Slice
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class FilterItemResolver implements GraphQLResolver<FilterItem> {

    String name(FilterItem item, LanguageTag languageTeg) {
        item.name.call(languageTeg)
    }

}
