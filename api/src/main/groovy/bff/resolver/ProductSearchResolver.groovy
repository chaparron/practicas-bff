package bff.resolver

import bff.bridge.RecommendedOrderBridge
import bff.model.GetFavoriteProductsInput
import bff.model.ProductSearch
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductSearchResolver implements GraphQLResolver<ProductSearch>{

    @Autowired
    RecommendedOrderBridge recommendedOrderBridge

    Boolean favorite(ProductSearch productSearch){
        def favorites = recommendedOrderBridge.getFavoriteProducts(new GetFavoriteProductsInput(accessToken: productSearch.accessToken))
        favorites.any{it.productId.longValue() == productSearch.id.longValue()}
    }

}
