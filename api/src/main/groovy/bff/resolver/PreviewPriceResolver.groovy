package bff.resolver

import bff.model.CommercialPromotion
import bff.model.Money
import bff.model.PreviewPrice
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PreviewPriceResolver implements GraphQLResolver<PreviewPrice> {

    @Autowired
    MoneyService moneyService

    Money valueMoney(PreviewPrice previewPrice) {
        moneyService.getMoneyByCountry(previewPrice.countryId, previewPrice.value)
    }

    Money unitValueMoney(PreviewPrice previewPrice) {
        moneyService.getMoneyByCountry(previewPrice.countryId, previewPrice.unitValue)
    }

    @Deprecated
    CommercialPromotion commercialPromotion(PreviewPrice price) {
        (price.commercialPromotions
                .flatMap { it.discount }
                .map { new CommercialPromotion(it) } | {
            price.commercialPromotions
                    .flatMap { it.freeProduct }
                    .map { new CommercialPromotion(it) }
        }).orElse(null)
    }

}
