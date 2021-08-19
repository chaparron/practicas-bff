package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.OrderItem
import bff.model.PartialSummary
import bff.model.Product
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderItemResolver implements GraphQLResolver<OrderItem> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    Product product(OrderItem orderItem) {
        supplierOrderBridge.getProductByOrderItem(orderItem.accessToken, orderItem.id)
    }

    PartialSummary partialSummary(OrderItem orderItem) {
        orderItem?.partialSummary ?: supplierOrderBridge.getPartialSummaryByOrderItem(orderItem.accessToken, orderItem.id)
    }

}
