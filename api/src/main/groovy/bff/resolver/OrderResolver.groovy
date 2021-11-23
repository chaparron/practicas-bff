package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.bridge.OrderBridge
import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderResolver implements GraphQLResolver<Order> {

    @Autowired
    OrderBridge orderBridge

    @Autowired
    CountryBridge countryBridge

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
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.total_credits)
    }

    Money totalMoneyMoney(Order order) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.total_money)
    }

    Money totalServiceFeeMoney(Order order) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.total_service_fee)
    }

    Money totalPendingMoney(Order order) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.total_pending)
    }

    Money totalDiscountsUsedMoney(Order order) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.total_discounts_used)
    }

    Money totalWabipayMoney(Order order) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.total_wabipay)
    }

    Money subTotalMoney(Order order) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.subTotal)
    }

    Money discountsMoney(Order order) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.discounts)
    }

    Money totalMoney(Order order) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(order.accessToken)).currency.code, order.total_money)
    }

}
