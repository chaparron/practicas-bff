package bff.model

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.time.OffsetDateTime

@Component
@Slf4j
class CouponsQuery implements GraphQLQueryResolver {

    RedeemableCouponsResponse redeemableCoupons(RedeemableCouponsRequest request) {
        return new RedeemableCouponsResponse(
                coupons: [
                        new Coupon(
                                code: UUID.randomUUID().toString(),
                                description: "a sample coupon",
                                validUntil: OffsetDateTime.now().plusDays(7)
                        )
                ]
        )
    }

}
