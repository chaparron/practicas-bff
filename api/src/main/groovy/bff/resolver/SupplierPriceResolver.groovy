package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.model.Money
import bff.model.SupplierPrice
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierPriceResolver implements GraphQLResolver<SupplierPrice> {

    @Autowired
    CountryBridge countryBridge

    Money priceMoney(SupplierPrice supplierPrice) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierPrice.accessToken)).currency.code, supplierPrice.price)
    }
}
