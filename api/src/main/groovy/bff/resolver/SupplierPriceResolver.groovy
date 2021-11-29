package bff.resolver

import bff.model.Money
import bff.model.SupplierPrice
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierPriceResolver implements GraphQLResolver<SupplierPrice> {

    @Autowired
    MoneyService moneyService

    Money priceMoney(SupplierPrice supplierPrice) {
        moneyService.getMoney(supplierPrice.accessToken, supplierPrice.price)
    }
}
