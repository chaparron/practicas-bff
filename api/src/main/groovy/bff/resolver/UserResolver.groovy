package bff.resolver

import bff.bridge.MarketingBridge
import bff.model.User
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserResolver implements GraphQLResolver<User> {

    @Autowired
    MarketingBridge marketingBridge

    String trackingId(User user){
        if (!user.accessToken){
            return null
        }
        marketingBridge.getTrackingId(user.accessToken, user.username)
    }

}
