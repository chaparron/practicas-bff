package bff.resolver

import bff.bridge.OrderBridge
import bff.bridge.ProductBridge
import bff.model.Brand
import bff.model.Category
import bff.model.Manufacturer
import bff.model.OrderItemProduct
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderItemProductResolver implements GraphQLResolver<OrderItemProduct> {

    @Autowired
    ProductBridge productBridge

    Category category(OrderItemProduct orderItemProduct) {
        productBridge.getCategoryByProductId(orderItemProduct.accessToken, orderItemProduct.id)
    }

    Manufacturer manufacturer(OrderItemProduct orderItemProduct) {
        productBridge.getManufacturerByProductId(orderItemProduct.accessToken, orderItemProduct.id)
    }

    Brand brand(OrderItemProduct orderItemProduct) {
        productBridge.getBrandByProductId(orderItemProduct.accessToken, orderItemProduct.id)
    }


}
