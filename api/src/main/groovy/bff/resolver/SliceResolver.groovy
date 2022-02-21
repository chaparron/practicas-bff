package bff.resolver


import bff.model.Slice
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class SliceResolver implements GraphQLResolver<Slice> {

    String name(Slice slice, LanguageTag languageTeg) {
        slice.name.call(languageTeg)
    }

}
