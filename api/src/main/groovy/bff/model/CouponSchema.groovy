package bff.model

import groovy.transform.Immutable

import java.time.OffsetDateTime

@Immutable
class RedeemableCouponsRequest {
    String accessToken
    List<ProductCartItemInput> items
    BigDecimal totalPrice
}

class Coupon {
    String code
    String description
    OffsetDateTime validUntil
}

class RedeemableCouponsResponse {
    List<Coupon> coupons
}