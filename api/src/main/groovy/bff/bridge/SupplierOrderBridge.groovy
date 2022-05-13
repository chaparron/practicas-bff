package bff.bridge

import bff.model.*

interface SupplierOrderBridge {

    Supplier getSupplierBySupplierOrderId(String accessToken, Long supplierOrderId)

    List<OrderItem> getOrderItemsBySupplierOrderId(String accessToken, Long supplierOrderId)

    RatingEntry getRatingBySupplierOrderId(String accessToken, Long supplierOrderId)

    Order getOrderBySupplierOrderId(String accessToken, Long supplierOrderId)

    Product getProductByOrderItem(String accessToken, Long orderItemId)

    PartialSummary getPartialSummaryByOrderItem(String accessToken, Long orderItemId)

    List<AppliedPromotionResponse> getPromotionsBySupplierOrderId(String accessToken, Long supplierOrderId)

}