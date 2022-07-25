package bff.model

import groovy.transform.Immutable

@Immutable
class FindMyCouponsInput {
    String accessToken
    BigDecimal totalPrice
    List<ProductCartItemInput> items
}

class Coupon {
    String code
    String description
    TimestampOutput expires
}

class CouponResponse {
    List<Coupon> coupons
}