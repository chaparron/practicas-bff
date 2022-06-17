package bff.resolver


import bff.model.Discount
import bff.model.MostSearchedTerm
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class MostSearchedTermResolver implements GraphQLResolver<MostSearchedTerm> {

    String label(MostSearchedTerm term, LanguageTag languageTag) {
        term.label.call(languageTag)
    }

}
