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

class PromotionFailed implements PromotionResult {
    PromotionFailedReason reason
}

enum PromotionFailedReason {
    INVALID_COUNTRY_ID,
    INVALID_LOCATION,
    NOT_FOUND

    def build() {
        new PromotionFailed(reason: this)
    }
}

enum GetLandingPromotionFailedReason {
    NOT_FOUND,
    INVALID_LOCATION,
    INVALID_COUNTRY_ID,

    def build() {
        new GetLandingPromotionFailed(reason: this)
    }

}

class PromotionResponse implements PromotionResult {
    List<Promotion> content
}


class GetLandingPromotionInput {
    String accessToken
    String country_id
}

