package bff.resolver

import bff.bridge.CustomerBridge
import bff.bridge.OrderBridge
import bff.bridge.ProductBridge
import bff.model.Rating
import bff.model.Supplier
import bff.model.SupplierOrder
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RatingResolver implements GraphQLResolver<Rating> {

    @Autowired
    ProductBridge productBridge

    @Autowired
    OrderBridge orderBridge


    SupplierOrder supplierOrder(Rating rating) {
        orderBridge.getSupplierOrderById(rating.or)
    }

    Supplier getSupplier(Rating rating) {
        productBridge.getSupplierById(rating.accessToken, rating.supplier.id)
    }

}