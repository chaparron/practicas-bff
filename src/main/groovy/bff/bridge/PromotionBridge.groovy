package bff.bridge

import bff.model.PromotionInput
import bff.model.PromotionResponse

interface PromotionBridge {

    PromotionResponse getAll(PromotionInput promotionInput)

}