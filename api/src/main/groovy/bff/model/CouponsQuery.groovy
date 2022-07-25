package bff.model

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Component
@Slf4j
class CouponsQuery implements GraphQLQueryResolver {

    CouponResponse findMyCoupons(FindMyCouponsInput input) {
        return new CouponResponse(coupons: [])
    }
}
