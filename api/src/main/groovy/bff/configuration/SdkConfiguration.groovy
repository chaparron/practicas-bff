package bff.configuration

import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.sdk.GroceryListing
import bff.bridge.sdk.credits.HttpCreditService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import wabi2b.grocery.listing.sdk.Sdk
import wabi2b.sdk.credits.HttpSupplierCreditsSdk

@Configuration
class SdkConfiguration {

    @Value('${grocery.listing.endpoint:}')
    String groceryListingEndpoint
    @Value('${supplier.credits.endpoint:}')
    String creditsEndpoint
    @Autowired
    CountryBridge countryBridge
    @Autowired
    CustomerBridge customerBridge
    @Autowired
    RestOperations client

    @Bean
    GroceryListing groceryListing() {
        new GroceryListing(
                sdk: new Sdk(client, groceryListingEndpoint.toURI()),
                countryBridge: countryBridge,
                customerBridge: customerBridge
        )
    }

    @Bean
    HttpCreditService creditService(){
        new HttpCreditService(
                creditsSdk: new HttpSupplierCreditsSdk.Builder().withBaseURI(creditsEndpoint.toURI()).build()
        )
    }

}
