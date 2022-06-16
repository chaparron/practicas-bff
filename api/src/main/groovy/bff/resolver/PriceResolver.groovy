package bff.resolver

import bff.bridge.ProductBridge
import bff.model.CommercialPromotion
import bff.model.Money
import bff.model.Price
import bff.model.Supplier
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PriceResolver implements GraphQLResolver<Price> {

    @Autowired
    ProductBridge productBridge

    @Autowired
    MoneyService moneyService

    Supplier supplier(Price price) {
        price.supplier.id && !price.supplier.name ? productBridge.getSupplierById(price.accessToken, price.supplier.id) : price.supplier
    }

    Money valueMoney(Price price) {
        moneyService.getMoney(price.accessToken, price.value)
    }

    Money unitValueMoney(Price price) {
        moneyService.getMoney(price.accessToken, price.unitValue)
    }

    @Deprecated
    CommercialPromotion commercialPromotion(Price price) {
        (price.commercialPromotions
                .flatMap { it.discount }
                .map { new CommercialPromotion(it) } | {
            price.commercialPromotions
                    .flatMap { it.freeProduct }
                    .map { new CommercialPromotion(it) }
        }).orElse(null)
    }

}
