package bff.configuration

import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.sdk.Cms
import bff.bridge.sdk.GroceryListing
import bff.bridge.sdk.credits.HttpCreditService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import org.springframework.web.reactive.function.client.WebClient
import wabi2b.cms.sdk.Sdk as CmsSdk
import wabi2b.grocery.listing.sdk.Sdk as GroceryListingSdk
import wabi2b.sdk.credits.HttpSupplierCreditsSdk
import wabi2b.sdk.regional.HttpRegionalConfigSdk
import wabi2b.sdk.regional.RegionalConfigSdk

@Configuration
class SdkConfiguration {

    @Value('${grocery.listing.endpoint:}')
    String groceryListingEndpoint
    @Value('${cms.endpoint:}')
    String cmsEndpoint
    @Value('${supplier.credits.endpoint:}')
    String creditsEndpoint
    @Autowired
    CountryBridge countryBridge
    @Autowired
    CustomerBridge customerBridge
    @Autowired
    RestOperations client
    @Value('${regional.config.url:}')
    URI regionalConfigUrl

    @Bean
    GroceryListing groceryListing() {
        new GroceryListing(
                sdk: new GroceryListingSdk(client, groceryListingEndpoint.toURI()),
                countryBridge: countryBridge,
                customerBridge: customerBridge
        )
    }

    @Bean
    Cms cms() {
        new Cms(
                sdk: new CmsSdk(client, cmsEndpoint.toURI()),
                customerBridge: customerBridge
        )
    }

    @Bean
    HttpCreditService creditService() {
        new HttpCreditService(
                creditsSdk: new HttpSupplierCreditsSdk.Builder().withBaseURI(creditsEndpoint.toURI()).build()
        )
    }

    @Bean
    RegionalConfigSdk regionalConfigSdk(WebClient.Builder webClientBuilder) {
        new HttpRegionalConfigSdk(
                regionalConfigUrl,
                webClientBuilder
        )
    }
}
