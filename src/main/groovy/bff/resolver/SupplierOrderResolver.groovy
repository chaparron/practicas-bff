package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierOrderResolver implements GraphQLResolver<SupplierOrder> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge


    Supplier supplier(SupplierOrder supplierOrder) {
        supplierOrderBridge.getSupplierBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    List<OrderItem> products(SupplierOrder supplierOrder) {
        supplierOrderBridge.getOrderItemsBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    RatingEntry ratings(SupplierOrder supplierOrder) {
        supplierOrderBridge.getRatingBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    Order order(SupplierOrder supplierOrder) {
        supplierOrderBridge.getOrderBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

}
