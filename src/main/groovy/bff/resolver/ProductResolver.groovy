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
import bff.model.Product
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
             productBridge.getPricesByProductId(product.accessToken, product.id)
        }
        catch(BadRequestErrorException ex) {
            PriceErrorReason.valueOf((String) ex.innerResponse).build()
        }
        catch(EntityNotFoundException ex) {
            PriceErrorReason.PRICE_NOT_FOUND.build()
        }

    }

    Price minUnitsPrice(Product product) {
        productBridge.getMinUnitsPriceByProductId(product.accessToken, product.id)
    }

    Price priceFrom(Product product) {
        productBridge.getPriceFromByProductId(product.accessToken, product.id)
    }

}
