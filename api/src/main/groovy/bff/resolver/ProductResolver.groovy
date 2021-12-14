package bff.resolver

import bff.bridge.ProductBridge
import bff.bridge.sdk.GroceryListing
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
    GroceryListing groceryListing

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
                    : getPrices(product)
        }
        catch (Exception ex) {
            product.prices = []
        }
    }

    Price minUnitsPrice(Product product) {
        if (product.minUnitsPrice) {
            return product.minUnitsPrice
        } else {
            product.prices = getPrices(product)
            return product.prices.min { Price a, Price b ->
                (a.minUnits == b.minUnits) ? a.unitValue <=> b.unitValue : a.minUnits <=> b.minUnits
            }
        }
    }

    Price priceFrom(Product product) {
        if (product.priceFrom) {
            return product.priceFrom
        } else {
            product.prices = getPrices(product)
            return product.prices.min { it.value }
        }
    }

    private List<Price> getPrices(Product product) {
        try {
            return groceryListing.getProductById(product.accessToken, product.id.toInteger()).prices
        }
        catch (EntityNotFoundException ex) {
            return []
        }
    }

}
