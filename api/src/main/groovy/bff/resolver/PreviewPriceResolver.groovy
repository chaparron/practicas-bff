package bff.resolver

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
}
