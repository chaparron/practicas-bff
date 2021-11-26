package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.Money
import bff.model.OrderItem
import bff.model.PartialSummary
import bff.model.Product
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderItemResolver implements GraphQLResolver<OrderItem> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    @Autowired
    MoneyService moneyService

    Product product(OrderItem orderItem) {
        supplierOrderBridge.getProductByOrderItem(orderItem.accessToken, orderItem.id)
    }

    PartialSummary partialSummary(OrderItem orderItem) {
        orderItem?.partialSummary ?: supplierOrderBridge.getPartialSummaryByOrderItem(orderItem.accessToken, orderItem.id)
    }

    Money priceMoney(OrderItem orderItem) {
        moneyService.getMoney(orderItem.accessToken, orderItem.price)
    }

    Money subtotalMoney(OrderItem orderItem) {
        moneyService.getMoney(orderItem.accessToken, orderItem.subtotal)
    }

}
