package bff.resolver

import bff.bridge.ProductBridge
import bff.bridge.sdk.GroceryListing
import bff.configuration.BadRequestErrorException
import bff.configuration.EntityNotFoundException
import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import static bff.support.DataFetchingEnvironments.experimentalMode
import static java.util.Optional.ofNullable

@Component
class ProductResolver implements GraphQLResolver<Product> {

    @Autowired
    ProductBridge productBridge
    @Autowired
    GroceryListing groceryListing
    @Value('${grocery.listing.countries:}')
    List<String> groceryListingEnabledCountries

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

    List<Price> prices(Product product, DataFetchingEnvironment dfe) {
        try {
            product.prices
                    ? product.prices
                    : getPrices(product, dfe)
        }
        catch (Exception ex) {
            product.prices = []
        }
    }

    Price minUnitsPrice(Product product, DataFetchingEnvironment dfe) {
        if (product.minUnitsPrice) {
            return product.minUnitsPrice
        } else {
            product.prices = getPrices(product, dfe)
            return product.prices.min { Price a, Price b ->
                (a.minUnits == b.minUnits) ? a.unitValue <=> b.unitValue : a.minUnits <=> b.minUnits
            }
        }
    }

    Price priceFrom(Product product, DataFetchingEnvironment dfe) {
        if (product.priceFrom) {
            return product.priceFrom
        } else {
            product.prices = getPrices(product, dfe)
            return product.prices.min { it.value }
        }
    }

    private List<Price> getPrices(Product product, DataFetchingEnvironment dfe) {
        try {
            return isGroceryListingEnabled(dfe, { product.country_id })
                    ? groceryListing.getProductById(product.accessToken, product.id.toInteger()).prices
                    : ofNullable(productBridge.getPricesByProductId(product.accessToken, product.id)).orElse([])
        }
        catch (EntityNotFoundException ex) {
            return []
        }
        catch (BadRequestErrorException ex) { // Maybe no supplier price found
            return []
        }
    }

    private def isGroceryListingEnabled(DataFetchingEnvironment dfe, Closure<String> country) {
        (experimentalMode(dfe) || ofNullable(groceryListingEnabledCountries).orElse([]).contains(country()))
    }

}
