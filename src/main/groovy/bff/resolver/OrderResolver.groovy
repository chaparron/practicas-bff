package bff.resolver

import bff.bridge.OrderBridge
import bff.model.Address
import bff.model.Customer
import bff.model.Order
import bff.model.SupplierOrder
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderResolver implements GraphQLResolver<Order> {

    @Autowired
    OrderBridge orderBridge

    Customer customer(Order order) {
        orderBridge.getCustomerOrder(order.accessToken, order.id)
    }

    Address deliveryAddress(Order order) {
        orderBridge.getDeliveryAddress(order.accessToken, order.id)
    }

    List<SupplierOrder> supplierOrders(Order order) {
        orderBridge.getSupplierOrders(order.accessToken, order.id)
    }

}