package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.OrderItem
import bff.model.OrderItemProduct
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderItemResolver implements  GraphQLResolver<OrderItem> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    OrderItemProduct product(OrderItem orderItem) {
        supplierOrderBridge.getProductByOrderItem(orderItem.accessToken, orderItem.id)
    }

}
