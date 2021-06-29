package bff.resolver

import bff.bridge.ProductBridge
import bff.bridge.RecommendedOrderBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.EntityNotFoundException
import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductResolver implements GraphQLResolver<Product> {

    @Autowired
    ProductBridge productBridge

    @Autowired
    RecommendedOrderBridge recommendedOrderBridge

    Category category(Product product) {
        productBridge.getCategoryByProductId(product.accessToken, product.id)
    }

    Brand brand(Product product) {
        product.brand ?: productBridge.getBrandByProductId(product.accessToken, product.id)
    }

    List<Feature> features(Product product) {
        productBridge.getFeaturesByProductId(product.accessToken, product.id)
    }

    List<Image> images(Product product) {
        productBridge.getImagesByProductId(product.accessToken, product.id)
    }

    Manufacturer manufacturer(Product product) {
        productBridge.getManufacturerByProductId(product.accessToken, product.id)
    }

    List<Price> prices(Product product) {
        try {
            product.prices
                    ? product.prices
                    : productBridge.getPricesByProductId(product.accessToken, product.id)
        }
        catch (Exception ex) {
            product.prices = null
        }
    }

    Price minUnitsPrice(Product product) {

        if (product.minUnitsPrice) {
            return product.minUnitsPrice
        } else if (!product.prices) {
            try {
                product.prices = productBridge.getPricesByProductId(product.accessToken, product.id)
            }
            catch (EntityNotFoundException ex) {
                product.prices = null
            }
            catch(BadRequestErrorException ex) { // Maybe no supplier price found
                product.prices = null
            }
        }


        def p = product.prices
        if (p && !p.isEmpty()) {
            Price minPriceUnits = p.min { it.minUnits }
            List<Price> shared = p.findAll { it.minUnits == minPriceUnits.minUnits }
            return shared.min { it.value }
        }
        null
    }

    Price priceFrom(Product product) {
        if (product.priceFrom) {
            return product.priceFrom
        } else if (!product.prices) {
            try {
                product.prices = productBridge.getPricesByProductId(product.accessToken, product.id)
            }
            catch (EntityNotFoundException ex) {
                product.prices = null
            }
            catch(BadRequestErrorException ex) { // Maybe no supplier price found
                product.prices = null
            }
        }
        def p = product.prices
        if (p && !p.isEmpty()) {
            return p.min { it.value }
        }
        null

    }

    Boolean favorite(Product product){
        def favorites = recommendedOrderBridge.getFavoriteProducts(new GetFavoriteProductsInput(accessToken: product.accessToken))
        for (favorite in favorites){
            if (favorite.productId.longValue() == product.id.longValue()){
                return true
            }
        }
        return false
    }

}
