package bff.bridge

import bff.model.*

interface OrderBridge {

    void cancel(CancelOrderInput cancelOrderInput)

    CustomerOrdersResponse findCustomerOrders(FindOrdersInput findOrdersInput)

    Address getDeliveryAddress(String accessToken, Long orderId)

    List<SupplierOrder> getSupplierOrders(String accessToken, Long orderId)

    Customer getCustomerOrder(String accessToken, Long orderId)

    void placeOrder(String accessToken, List<OrderInput> orders)

    SupplierOrder getSupplierOrder(String accessToken, Long supplierOrderId)
}