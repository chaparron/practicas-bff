package bff.resolver

import bff.model.CmsButton
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class CmsButtonResolver implements GraphQLResolver<CmsButton> {

    String label(CmsButton button, LanguageTag languageTag) {
        button.label.getOrDefault(languageTag)
    }

}
