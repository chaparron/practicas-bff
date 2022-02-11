package bff.bridge

import bff.model.*

interface PromotionBridge {

    PromotionResponse getAll(PromotionInput promotionInput)

    PromotionResponse previewPromotions(CoordinatesInput coordinatesInput)

    Promotion getLandingPromotion(GetLandingPromotionInput countryId)

    Promotion previewLandingPromotion(CoordinatesInput coordinatesInput)
}