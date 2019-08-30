package bff.bridge

import bff.model.GetLandingPromotionInput
import bff.model.Promotion
import bff.model.PromotionInput
import bff.model.PromotionResponse

interface PromotionBridge {

    PromotionResponse getAll(PromotionInput promotionInput)

    Promotion getLandingPromotion(GetLandingPromotionInput countryId)
}