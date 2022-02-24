package bff.model

interface CommercialPromotionType {}

class CommercialPromotion {
    String id
    String description
    TimestampOutput expiration
    CommercialPromotionType type
}

class DiscountStep {
    Integer from
    Integer to
    BigDecimal value
    BigDecimal unitValue
    BigDecimal percentage
    String accessToken
}

class Discount implements CommercialPromotionType {
    Boolean progressive
    List<DiscountStep> steps

    def minValue() { steps.min { it.value }.value }

    def minUnitValue() { steps.min { it.unitValue }.unitValue }
}

class FreeProduct implements CommercialPromotionType {
    Long id
    String title
    List<Image> images
    Display display
    Boolean favorite
}