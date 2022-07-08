package bff.resolver

import bff.bridge.OrderBridge
import bff.model.*
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderResolver implements GraphQLResolver<Order> {

    @Autowired
    OrderBridge orderBridge

    @Autowired
    MoneyService moneyService

    Customer customer(Order order) {
        order.customer?: orderBridge.getCustomerOrder(order.accessToken, order.id)
    }

    Address deliveryAddress(Order order) {
        orderBridge.getDeliveryAddress(order.accessToken, order.id)
    }

    List<SupplierOrder> supplierOrders(Order order) {
        orderBridge.getSupplierOrders(order.accessToken, order.id)
    }

    List<OrderCancellation> ordersCancellations(Order order) {
        orderBridge.getOrdersCancellation(order.accessToken, order.id)
    }

    Money totalCreditsMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.total_credits)
    }

    Money totalMoneyMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.total_money)
    }

    Money totalServiceFeeMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.total_service_fee)
    }

    Money totalPendingMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.total_pending)
    }

    Money totalDiscountsUsedMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.total_discounts_used)
    }

    Money totalWabipayMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.total_wabipay)
    }

    Money subTotalMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.subTotal)
    }

    Money discountsMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.discounts)
    }

    Money totalMoney(Order order) {
        moneyService.getMoney(order.accessToken, order.total_money)
    }
}
