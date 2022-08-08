package bff.configuration

import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.sdk.Cms
import bff.bridge.sdk.ExternalPayments
import bff.bridge.sdk.GroceryListing
import bff.bridge.sdk.credits.HttpCreditService
import bnpl.sdk.BnPlSdk
import bnpl.sdk.HttpBnPlSdk
import com.wabi2b.externalorders.sdk.ExternalOrderClient
import com.wabi2b.externalorders.sdk.ExternalOrderHttpClient
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.HttpDigitalPaymentsSdk
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
import wabi2b.payments.sdk.client.WalletSdk
import wabi2b.payments.sdk.client.impl.HttpWalletSdk
import wabi2b.payments.sdk.client.impl.WabiPaymentSdk
import wabi2b.sdk.api.HttpWabi2bSdk
import wabi2b.sdk.api.Wabi2bSdk
import wabi2b.sdk.credits.HttpSupplierCreditsSdk
import wabi2b.sdk.customers.customer.CustomersSdk
import wabi2b.sdk.customers.customer.HttpCustomersSdk
import wabi2b.sdk.featureflags.FeatureFlagsSdk
import wabi2b.sdk.featureflags.HttpFeatureFlagSdk
import wabi2b.sdk.integration.HttpMarketingConsentSdk
import wabi2b.sdk.integration.MarketingConsentSdk
import wabi2b.sdk.regional.RegionalConfigSdk
import wabi2b.sdk.regional.RegionalConfigSdkFactory

import java.time.Duration

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
    @Value('${customers.url}')
    String customersUrl
    @Value('${external.orders.url}')
    String externalOrdersUrl
    @Value('${payments.url:}')
    String paymentsUrl
    @Value('${feature.flags.url}')
    String featureFlagsUrl
    @Value('${third.party.url:}')
    URI thirdPartyUrl
    @Value('${bnpl.credits.url:}')
    URI wabi2bBnplCreditsURI
    @Value('${digital.payments.url:}')
    URI digitalPaymentsURI


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
                siteRoot: siteRoot,
                messageSource: messageSource
        )
    }

    @Bean
    ExternalPayments payments() {
        new ExternalPayments(sdk: new WabiPaymentSdk(paymentsUrl))
    }

    @Bean
    HttpCreditService creditService() {
        new HttpCreditService(
                creditsSdk: new HttpSupplierCreditsSdk.Builder().withBaseURI(creditsEndpoint.toURI()).build()
        )
    }

    @Bean
    RegionalConfigSdk regionalConfigSdk(
            CacheConfigurationProperties cacheConfiguration,
            WebClient.Builder webClientBuilder
    ) {
        new RegionalConfigSdkFactory(
                defaultExpirationTime: Duration.ofMinutes(cacheConfiguration.regionalConfig)
        ).build(regionalConfigUrl.toURI(), webClientBuilder)
    }

    @Bean
    Wabi2bSdk wabi2bSdk() {
        return new HttpWabi2bSdk.Builder().withBaseURI(wabi2bApiURI).build()
    }

    @Bean
    CustomersSdk customersSdk() {
        return new HttpCustomersSdk(customersUrl)
    }

    @Bean
    ExternalOrderClient externalOrderClient() {
        return new ExternalOrderHttpClient(externalOrdersUrl)
    }

    @Bean
    MarketingConsentSdk marketingConsentSdk() {
        return new HttpMarketingConsentSdk.Builder().withBaseURI(thirdPartyUrl).build()
    }

    @Bean
    FeatureFlagsSdk featureFlagClient() {
        return (new HttpFeatureFlagSdk.Builder()).withBaseURI(URI.create(featureFlagsUrl)).build()
    }

    @Bean
    BnPlSdk bnplSdk() {
        return new HttpBnPlSdk(wabi2bBnplCreditsURI)
    }

    @Bean
    DigitalPaymentsSdk digitalPaymentsSdk() {
        return new HttpDigitalPaymentsSdk(digitalPaymentsURI)
    }

    @Bean
    WalletSdk walletSdk() {
        new HttpWalletSdk(paymentsUrl)
    }
}
