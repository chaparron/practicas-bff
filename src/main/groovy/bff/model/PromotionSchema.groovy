package bff.model

class PromotionInput {
    String country_id
    String accessToken
}

interface PromotionResult {
}

class Promotion implements PromotionResult {
    Long id
    String banner
    String banner_mobile
    String tag
    String country_id
}

class GetLandingPromotionFailed implements PromotionResult {
    GetLandingPromotionFailedReason reason
}

enum GetLandingPromotionFailedReason {
    NOT_FOUND

    def build() {
        new GetLandingPromotionFailed(reason: this)
    }
}

class PromotionResponse {
    List<Promotion> content
}

class GetLandingPromotionInput {
    String accessToken
    String country_id
}

