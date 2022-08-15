package bff.model

import bff.bridge.sdk.GroceryListing
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class CouponsQuery implements GraphQLQueryResolver {

    @Autowired
    GroceryListing groceryListing

    RedeemableCouponsResponse redeemableCoupons(RedeemableCouponsRequest request) {
        return groceryListing.findRedeemableCoupons(request)
    }

}
