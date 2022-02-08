package bff.model

interface CommercialPromotionType {}

class CommercialPromotion {
    String id
    String description
    CommercialPromotionType type
}

class DiscountStep {
    Integer from
    Integer to
    BigDecimal value
    BigDecimal percentage
    String accessToken
}

class Discount implements CommercialPromotionType {
    List<DiscountStep> steps
}

class FreeProduct implements CommercialPromotionType {
    Long id
    String title
    List<Image> images
    Display display
    Boolean favorite
}