package bff.bridge

import bff.model.Address
import bff.model.CancelOrderInput
import bff.model.Customer
import bff.model.CustomerOrdersResult
import bff.model.FindOrdersInput
import bff.model.SupplierOrder

interface OrderBridge {

    void cancel(CancelOrderInput cancelOrderInput)

    CustomerOrdersResult findCustomerOrders(FindOrdersInput findOrdersInput)

    Address getDeliveryAddress(String accessToken, Long orderId)

    List<SupplierOrder> getSupplierOrders(String accessToken, Long orderId)

    Customer getCustomerOrder(String accessToken, Long orderId)
}