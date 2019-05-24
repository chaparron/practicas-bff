package bff.bridge

import bff.model.CancelOrderInput
import bff.model.CustomerOrdersResult
import bff.model.FindOrdersInput

interface OrderBridge {

    void cancel(CancelOrderInput cancelOrderInput)

    CustomerOrdersResult findCustomerOrders(FindOrdersInput findOrdersInput)
}