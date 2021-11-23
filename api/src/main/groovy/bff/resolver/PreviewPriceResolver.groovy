package bff.resolver

import bff.bridge.CountryBridge
import bff.model.Money
import bff.model.PreviewPrice
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PreviewPriceResolver implements GraphQLResolver<PreviewPrice> {

    @Autowired
    CountryBridge countryBridge

    Money valueMoney(PreviewPrice previewPrice) {
        new Money(countryBridge.getCountry(previewPrice.countryId).currency.code, previewPrice.value)
    }

    Money unitValueMoney(PreviewPrice previewPrice) {
        new Money(countryBridge.getCountry(previewPrice.countryId).currency.code, previewPrice.unitValue)
    }
}
