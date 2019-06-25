package bff.model

class PromotionInput {
    String country_id
    String accessToken
}

class Promotion {
    Long id
    String banner
    String tag
    String country_id
}

class PromotionResponse{
    List<Promotion> content
    Headers headers
}
