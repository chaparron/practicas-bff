package bff.bridge

import bff.model.*

interface OrderBridge {

    OrderUpdateResult cancel(CancelOrderInput cancelOrderInput)

    void cancelReason(CancelOrderInput cancelOrderInput)

    CustomerOrdersResponse findCustomerOrders(FindOrdersInput findOrdersInput)

    Address getDeliveryAddress(String accessToken, Long orderId)

    List<SupplierOrder> getSupplierOrders(String accessToken, Long orderId)

    Customer getCustomerOrder(String accessToken, Long orderId)

    void placeOrder(String accessToken, List<OrderInput> orders, String wabiPayAccessToken)

    SupplierOrder getSupplierOrder(String accessToken, Long supplierOrderId)

    SummaryResult getOrderSummary(String accessToken, List<SupplierCartProductInput> productsSupplier, String wabiPayAccessToken)

    List<OrderCancellation> getOrdersCancellation(String accessToken, Long orderId)

}