package bff.configuration

import bff.bridge.CustomerBridge
import bff.bridge.sdk.GroceryListing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import wabi2b.grocery.listing.sdk.Sdk

@Configuration
class SdkConfiguration {

    @Value('${grocery.listing.endpoint:}')
    String groceryListingEndpoint
    @Autowired
    CustomerBridge customerBridge
    @Autowired
    RestOperations client

    @Bean
    GroceryListing groceryListing() {
        new GroceryListing(
                sdk: new Sdk(client, groceryListingEndpoint.toURI()),
                customerBridge: customerBridge
        )
    }

}
