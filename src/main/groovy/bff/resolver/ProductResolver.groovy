package bff.resolver

import bff.bridge.ProductBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.EntityNotFoundException
import bff.model.Brand
import bff.model.Category
import bff.model.Feature
import bff.model.Image
import bff.model.Manufacturer
import bff.model.Price
import bff.model.PriceErrorReason
import bff.model.PriceResult
import bff.model.Prices
import bff.model.Product
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.swing.text.html.parser.Entity

@Component
class ProductResolver implements GraphQLResolver<Product> {

    @Autowired
    ProductBridge productBridge

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

    PriceResult prices(Product product) {
        try {
            product.prices
                ? new Prices(prices: product.prices)
                : new Prices(prices: productBridge.getPricesByProductId(product.accessToken, product.id))
        }
        catch (BadRequestErrorException ex) {
            PriceErrorReason.valueOf((String) ex.innerResponse).build()
        }
        catch (EntityNotFoundException ex) {
            PriceErrorReason.PRICE_NOT_FOUND.build()
        }

    }

    Price minUnitsPrice(Product product) {

        if (product.minUnitsPrice) {
            return product.minUnitsPrice
        } else if (!product.prices) {
            product.prices = productBridge.getPricesByProductId(product.accessToken, product.id)
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
        }
        def p = product.prices
        if (p && !p.isEmpty()) {
            return p.min { it.value }
        }
        null

    }

}
