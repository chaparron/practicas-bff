package bff.bridge

import bff.model.CoordinatesInput
import bff.model.GetLandingPromotionInput
import bff.model.Promotion
import bff.model.PromotionInput
import bff.model.PromotionResponse

interface PromotionBridge {

    PromotionResponse getAll(PromotionInput promotionInput)

    PromotionResponse previewPromotions(CoordinatesInput coordinatesInput)

    Promotion getLandingPromotion(GetLandingPromotionInput countryId)

    Promotion previewLandingPromotion(CoordinatesInput coordinatesInput)
}