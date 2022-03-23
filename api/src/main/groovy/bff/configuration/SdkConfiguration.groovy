package bff.configuration

import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.sdk.Cms
import bff.bridge.sdk.GroceryListing
import bff.bridge.sdk.credits.HttpCreditService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import org.springframework.web.reactive.function.client.WebClient
import wabi2b.cms.sdk.Sdk as CmsSdk
import wabi2b.grocery.listing.sdk.Sdk as GroceryListingSdk
import wabi2b.sdk.api.HttpWabi2bSdk
import wabi2b.sdk.api.Wabi2bSdk
import wabi2b.sdk.credits.HttpSupplierCreditsSdk
import wabi2b.sdk.regional.HttpRegionalConfigSdk
import wabi2b.sdk.regional.RegionalConfigSdk

@Slf4j
@Configuration
class SdkConfiguration {

    @Value('${grocery.listing.endpoint:}')
    String groceryListingEndpoint
    @Value('${cms.endpoint:}')
    String cmsEndpoint
    @Value('${supplier.credits.endpoint:}')
    String creditsEndpoint
    @Value('${regional.config.url:}')
    String regionalConfigUrl
    @Value('${site.root:}')
    String siteRoot
    @Value('${api.root}')
    URI wabi2bApiURI

    @Autowired
    CountryBridge countryBridge
    @Autowired
    CustomerBridge customerBridge
    @Autowired
    RestOperations client
    @Autowired
    MessageSource messageSource

    @Bean
    GroceryListing groceryListing() {
        new GroceryListing(
                sdk: new GroceryListingSdk(client, groceryListingEndpoint.toURI()),
                countryBridge: countryBridge,
                customerBridge: customerBridge,
                messageSource: messageSource
        )
    }

    @Bean
    Cms cms() {
        new Cms(
                sdk: new CmsSdk(client, cmsEndpoint.toURI()),
                customerBridge: customerBridge,
                siteRoot: siteRoot
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
        new HttpRegionalConfigSdk(regionalConfigUrl.toURI(), webClientBuilder)
    }

    @Bean
    Wabi2bSdk wabi2bSdk(){
        return new HttpWabi2bSdk.Builder().withBaseURI(wabi2bApiURI).build()
    }
}
