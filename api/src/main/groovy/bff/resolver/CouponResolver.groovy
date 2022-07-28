package bff.resolver


import bff.model.Coupon
import bff.model.TimestampOutput
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Component
@Slf4j
class CouponResolver implements GraphQLResolver<Coupon> {

    TimestampOutput validUntil(Coupon coupon) {
        new TimestampOutput(coupon.validUntil.toString())
    }

}