package bff.resolver


import bff.model.CommercialPromotion
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class CommercialPromotionResolver implements GraphQLResolver<CommercialPromotion> {

    String label(CommercialPromotion promotion, LanguageTag languageTag) {
        promotion.label.call(languageTag)
    }

}
