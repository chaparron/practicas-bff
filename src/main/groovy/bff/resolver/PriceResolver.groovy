package bff.resolver

import bff.bridge.ProductBridge
import bff.model.Price
import bff.model.Supplier
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PriceResolver implements GraphQLResolver<Price> {

    @Autowired
    ProductBridge productBridge

    Supplier supplier(Price price) {
        price.supplier.id && !price.supplier.name ? productBridge.getSupplierById(price.accessToken, price.supplier.id) : price.supplier
    }

}
