package bff.resolver

import bff.bridge.BnplBridge
import bff.bridge.OrderBridge
import bff.bridge.SupplierOrderBridge
import bff.model.*
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class OrderResolver implements GraphQLResolver<Order> {

    OrderBridge orderBridge
    SupplierOrderBridge supplierOrderBridge
    MoneyService moneyService
    SupplierOrderResolver supplierOrderResolver
    BnplBridge bnplBridge

    OrderResolver(OrderBridge orderBridge, SupplierOrderBridge supplierOrderBridge, MoneyService moneyService, SupplierOrderResolver supplierOrderResolver, BnplBridge bnplBridge) {
        this.orderBridge = orderBridge
        this.supplierOrderBridge = supplierOrderBridge
        this.moneyService = moneyService
        this.supplierOrderResolver = supplierOrderResolver
        this.bnplBridge = bnplBridge
    }

    Customer customer(Order order) {
        order.customer ?: orderBridge.getCustomerOrder(order.accessToken, order.id)
    }

    Address deliveryAddress(Order order) {
        orderBridge.getDeliveryAddress(order.accessToken, order.id)
    }

    List<SupplierOrder> supplierOrders(Order order) {
        orderBridge.getSupplierOrders(order.accessToken, order)
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

    List<PaymentMode> paymentMode(Order order) {
        List<PaymentMode> result = []

        supplierOrders(order).forEach {
            result.addAll(supplierOrderResolver.supportedPaymentProviders(it).collect {
                new PaymentMode(PaymentModeType.valueOf(it.code.name()))
            })
        }

        return result.unique()
    }

    MessageBox payLaterMessageBox(Order order) {
        def status = bnplBridge.customerStatus(order.accessToken)
        def supplierOrders = orderBridge.getSupplierOrders(order.accessToken, order)
        if (status.active) {
            return null
        } else {
            def atLeastOneSupplierSupportBnPl = supplierOrders.any {
                def supplier = supplierOrderBridge.getSupplierBySupplierOrderId(it.accessToken, it.id)
                bnplBridge.isSupplierOnboarded(supplier.id, it.accessToken)
            }
            if (atLeastOneSupplierSupportBnPl)
                new MessageBox("anIcon", "order.bnpl.messageBoxInfo.title", "order.bnpl.messageBoxInfo.description")
            else null
        }
    }

}
