package bff.resolver

import bff.model.ApplicationMode
import bff.model.Discount
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class DiscountResolver implements GraphQLResolver<Discount> {

    String label(Discount discount, LanguageTag languageTag) {
        discount.label.call(languageTag)
    }

    Boolean progressive(Discount discount) {
        switch (discount.applicationMode) {
            case ApplicationMode.SLABBED:
            case ApplicationMode.SLABBED_GLOBAL:
            case ApplicationMode.PROGRESSIVE:
            case ApplicationMode.PROGRESSIVE_GLOBAL:
                true
                break
            case ApplicationMode.LINEAL:
            case ApplicationMode.LINEAL_GLOBAL:
                false
                break
        }
    }

}
