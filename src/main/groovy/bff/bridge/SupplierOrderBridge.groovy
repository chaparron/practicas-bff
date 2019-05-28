package bff.bridge

import bff.model.*

interface SupplierOrderBridge {

    Supplier getSupplierBySupplierOrderId(String accessToken, Long supplierOrderId)

    List<OrderItem> getOrderItemsBySupplierOrderId(String accessToken, Long supplierOrderId)

    RatingEntry getRatingBySupplierOrderId(String accessToken, Long supplierOrderId)

    Order getOrderBySupplierOrderId(String accessToken, Long supplierOrderId)

    OrderItemProduct getProductByOrderItem(String accessToken, Long orderItemId)

}