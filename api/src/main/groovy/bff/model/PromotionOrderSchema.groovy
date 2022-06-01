package bff.model

class AppliedPromotionResponse {
    AppliedPromotionDetailResponse promotion
    List<Long> involvedCartItems
}

class AppliedPromotionDetailResponse {
    String id
    String description
    String code
    PromotionType type
    FreePromotionDetailResponse freeDetail
}

class FreePromotionDetailResponse {
    String ean
    Integer units
    Long quantity
    String title
    String image
}

class AppliedPromotionInput {
    PromotionType type
    List<TriggerCartItem> triggerCartItems
    ProductFreeItemInput product
    String promotionId
}

enum PromotionType {
    DISCOUNT, FREE
}