package bff

import bff.configuration.BridgeRestTemplateResponseErrorHandler
import bff.model.*
import com.coxautodev.graphql.tools.SchemaParserDictionary
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestOperations

@SpringBootApplication(exclude = SecurityAutoConfiguration)
class Main {
    static void main(String[] args) {
        SpringApplication.run(Main, args)
    }

    @Bean
    SchemaParserDictionary schemaParserDictionary() {
        new SchemaParserDictionary()
                .add(GenericCredentials.class)
                .add(ProfileCredentials.class)
                .add(RefreshCredentials.class)
                .add(UsernameRegistrationFailed.class)
                .add(ConfirmPasswordFailed.class)
                .add(ChangePasswordFailed.class)
                .add(CustomerErrorFailed.class)
                .add(ProductFailed.class)
                .add(RegisterFailed.class)
                .add(LoginFailed.class)
                .add(OrderUpdateFailed.class)
                .add(CustomerOrdersResponse.class)
                .add(Product.class)
                .add(Prices.class)
                .add(PriceFailed.class)
                .add(Cart.class)
                .add(CartFailed.class)
                .add(PlaceOrderFailed.class)
                .add(AddressFailed.class)
                .add(AddAddressFailed.class)
                .add(DeleteAddressFailed.class)
                .add(CustomerOrderFindFailed.class)
                .add(SignInFailed.class)
                .add(CustomerRateSupplierFailed.class)
                .add(CustomerReportRateFailed.class)
                .add(SupplierFailed.class)
                .add(SupplierOrderFailed.class)
                .add(GetHomeBrandsFailed.class)
                .add(GetHomeBrandsResult.class)
                .add(UploadDocumentFailed.class)
                .add(UploadedDocument.class)
                .add(OrderSummaryResponse.class)
                .add(SummaryFailed.class)
                .add(FinalOrderState.class)
                .add(Promotion.class)
                .add(GetLandingPromotionFailed.class)
    }
    /**
     *
     * @return restOperations with custom error handler
     */
    @Bean
    RestOperations restOperations() {
        new RestTemplateBuilder()
                .errorHandler(new BridgeRestTemplateResponseErrorHandler())
                .build()
    }
}


