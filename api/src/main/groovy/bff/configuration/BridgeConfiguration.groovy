package bff.configuration

import bff.bridge.*
import bff.bridge.http.*
import bff.bridge.sqs.DataRegisterBridgeImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations

@Configuration
class BridgeConfiguration {

    @Autowired
    RestOperations http

    @Value('${api.root}')
    URI root

    @Value('${phone.notifier.url}')
    String phone_notifier_url

    @Value('${notifications.url}')
    String notifications_url

    @Value('${marketing.bridge.url}')
    URI marketing_bridge_url

    @Bean
    AuthServerBridge authServerBridge() {
        new AuthServerBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    CustomerBridge customerBridge() {
        new CustomerBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    SearchBridge searchBridge() {
        new SearchBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    CategoryBridge categoryBridge() {
        new CategoryBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    OrderBridge orderBridge() {
        new OrderBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    SupplierHomeBridge supplierBridge() {
        new SupplierHomeBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    SupplierOrderBridge supplierOrder() {
        new SupplierOrderBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    BnplBridge bnplBridge() {
        new BnplBridgeImpl()
    }

    @Bean
    WalletBridge walletBridge()  {
        new WalletBridgeImpl()
    }

    @Bean
    ProductBridge productBridge() {
        new ProductBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    ValidationsBridge validationsBridge() {
        new ValidationsBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    BrandBridge brandBridge() {
        new BrandBridgeImpl(
            http: http,
            root: root
        )
    }

    @Bean
    CountryBridge countryBridge() {
        new CountryGatewayBridgeImpl()
    }

    @Bean
    DocumentBridge documentBridge() {
        new DocumentBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    PromotionBridge promotionBridge() {
        new PromotionBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    StateBridge stateBridge() {
        new StateGatewayBridgeImpl()
    }

    @Bean
    SiteConfigurationBridge siteConfigurationBridge() {
        return new SiteConfigurationBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    RecommendedOrderBridge recommendedOrderBridge() {
        new RecommendedOrderBridgeImpl(
                http: http,
                root: root
        )
    }

    @Bean
    PhoneNotifierBridge phoneNotifierBridge() {
        new PhoneNotifierBridgeImpl(phone_notifier_url)
    }

    @Bean
    NotificationBridge notificationBridge() {
        new NotificationBridgeImpl(notifications_url)
    }

    @Bean
    SupplierBridge suppliersBridge() {
        new SupplierBridgeImpl(
                root: root
        )
    }

    @Bean
    MarketingBridge marketingBridge() {
        new MarketingBridgeImpl(
                http: http,
                root: marketing_bridge_url
        )
    }

    @Bean
    static DataRegisterBridge dataRegisterBridge() {
        new DataRegisterBridgeImpl()
    }

    @Bean
    ThirdPartyBridge thirdPartyBridge() {
        new ThirdPartyBridgeImpl()
    }

    @Bean
    DigitalPaymentsBridge digitalPaymentsBridge() {
        new DigitalPaymentsBridgeImpl()
    }

    @Bean
    PaymentsBridge paymentsBridge() {
        new PaymentsBridgeImpl()
    }

}