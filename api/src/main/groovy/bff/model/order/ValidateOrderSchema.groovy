package bff.model.order

import bff.model.ProductFreeItemInput

class ValidateOrderInputV2 {
    String accessToken
    List<OrderInputV2> orders
}

class OrderInputV2 {
    Integer supplierId
    Long deliveryZoneId
    BigDecimal deliveryCost
    List<CartLineInput> products
    List<AppliedPromotionInputV2> appliedPromotions
}

class CartLineInput {
    Long productId
    Long units
    Integer quantity
    BigDecimal price
    BigDecimal totalPrice
}

class AppliedPromotionInputV2 {
    String type
    String promotionId
    List<TriggerCartItemV2> triggerCartItems
    Collection<ProductFreeItemInput> productsFreeSelected
}

class TriggerCartItemV2 {
    Integer productId
    Integer units
}
