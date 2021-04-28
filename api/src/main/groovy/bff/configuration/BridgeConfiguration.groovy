package bff.configuration

import bff.bridge.*
import bff.bridge.http.*
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
    SupplierBridge supplierBridge() {
        new SupplierBridgeImpl(
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
        new CountryBridgeImpl(
            http: http,
            root: root
        )
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
        new StateBridgeImpl(
            http: http,
            root: root
        )
    }

    @Bean
    SiteConfigurationBridge siteConfigurationBridge() {
        return new SiteConfigurationBridgeImpl(
                http: http,
                root: root
        )
    }

}