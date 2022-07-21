package bff.bridge

import bff.model.*
import bff.model.order.OrderInputV2
import bff.model.order.ValidateOrderInputV2

interface OrderBridge {

    OrderUpdateResult cancel(CancelOrderInput cancelOrderInput)

    void cancelReason(CancelOrderInput cancelOrderInput)

    CustomerOrdersResponse findCustomerOrders(FindOrdersInput findOrdersInput)

    CustomerOrderResponse findCustomerOrder(FindSupplierOrderInput findSupplierOrderInput)

    CustomerSupplierOrdersResponse findCustomerAndSupplierOrders(FindCustomerAndSupplierOrdersInput findOrderAndSupplierOrderInput)

    Address getDeliveryAddress(String accessToken, Long orderId)

    List<SupplierOrder> getSupplierOrders(String accessToken, Order order)

    Customer getCustomerOrder(String accessToken, Long orderId)

    def placeOrder(String accessToken, List<OrderInput> orders, String wabiPayAccessToken, List<String> coupons)

    def placeOrderV1(String accessToken, List<OrderInputV2> orders, String wabiPayAccessToken, List<String> coupons)

    SupplierOrder getSupplierOrder(String accessToken, Long supplierOrderId)

    SummaryResult getOrderSummary(String accessToken, List<SupplierCartProductInput> productsSupplier, String wabiPayAccessToken, List<String> coupons)

    List<OrderCancellation> getOrdersCancellation(String accessToken, Long orderId)

    @Deprecated
    ValidateOrderResponse validateOrder(ValidateOrderInput validateOrderInput)

    @Deprecated
    ValidateOrderResponseV1 validateOrder(ValidateOrderInputV1 validateOrderInput)

    ValidateOrderResponseV1 validateOrder(ValidateOrderInputV2 validateOrderInput)
}