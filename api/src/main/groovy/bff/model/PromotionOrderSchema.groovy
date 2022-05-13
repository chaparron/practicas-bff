package bff.model

class AppliedPromotionResponse {
    AppliedPromotionDetailResponse promotion
    List<String> involvedCartItems
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
    String image
}

enum PromotionType {
    DISCOUNT, FREE
}