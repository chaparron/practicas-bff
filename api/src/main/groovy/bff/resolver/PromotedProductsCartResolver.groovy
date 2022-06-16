package bff.resolver


import bff.model.CommercialPromotion
import bff.model.PromotedProductsCart
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class PromotedProductsCartResolver implements GraphQLResolver<PromotedProductsCart> {

    @Deprecated
    CommercialPromotion commercialPromotion(PromotedProductsCart cart) {
        (cart.commercialPromotions
                .discount
                .map { new CommercialPromotion(it) } | {
            cart.commercialPromotions.freeProduct.map { new CommercialPromotion(it) }
        }).orElse(null)
    }

}
