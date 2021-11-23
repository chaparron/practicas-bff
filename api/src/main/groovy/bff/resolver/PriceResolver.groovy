package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.bridge.ProductBridge
import bff.model.Money
import bff.model.Price
import bff.model.Supplier
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PriceResolver implements GraphQLResolver<Price> {

    @Autowired
    ProductBridge productBridge

    @Autowired
    CountryBridge countryBridge

    Supplier supplier(Price price) {
        price.supplier.id && !price.supplier.name ? productBridge.getSupplierById(price.accessToken, price.supplier.id) : price.supplier
    }

    Money valueMoney(Price price) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(price.accessToken)).currency.code, price.value)
    }

    Money unitValueMoney(Price price) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(price.accessToken)).currency.code, price.unitValue)
    }
}
