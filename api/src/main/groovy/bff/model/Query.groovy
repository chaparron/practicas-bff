package bff.model

import bff.bridge.*
import bff.bridge.sdk.GroceryListing
import bff.configuration.BadRequestErrorException
import bff.configuration.EntityNotFoundException
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * TODO: Representa todas las queries a graphql, tener en cuenta de dividirlo en mas de un resolver
 */
@Component
@Slf4j
class Query implements GraphQLQueryResolver {

    @Autowired
    AuthServerBridge authServerBridge
    @Autowired
    CustomerBridge customerBridge
    @Autowired
    ProductBridge productBridge
    @Autowired
    OrderBridge orderBridge
    @Autowired
    SupplierHomeBridge supplierBridge
    @Autowired
    BrandBridge brandBridge
    @Autowired
    ValidationsBridge validationsBridge
    @Autowired
    CountryBridge countryBridge
    @Autowired
    PromotionBridge promotionBridge
    @Autowired
    StateBridge stateBridge
    @Autowired
    SiteConfigurationBridge siteConfigurationBridge
    @Autowired
    CategoryBridge categoryBridge
    @Autowired
    RecommendedOrderBridge recommendOrderBridge
    @Autowired
    PhoneNotifierBridge phoneNotifierBridge
    @Autowired
    GroceryListing groceryListing

    Customer myProfile(CustomerInput customerInput) {
        customerBridge.myProfile(customerInput.accessToken)
    }

    VerifyEmailResult verifyEmail(VerifyEmailInput verifyEmailInput) {
        try {
            customerBridge.verifyEmail(verifyEmailInput)
            Void.SUCCESS
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    ResendVerifyEmailResult resendVerifyEmail(AccessTokenInput accessTokenInput) {
        try {
            customerBridge.resendVerifyEmail(accessTokenInput)
            Void.SUCCESS
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    ProductResult productDetail(ProductInput productInput) {
        try {
            groceryListing.getProductById(productInput.accessToken, productInput.productId)
        }
        catch (EntityNotFoundException ex) {
            ProductErrorReason.PRODUCT_NOT_FOUND.build()
        }
    }

    ProductResult productDetailByEan(ProductEanInput productInput) {
        try {
            productBridge.getProductByEan(productInput.accessToken, productInput.ean)
        }
        catch (BadRequestErrorException ex) {
            ProductErrorReason.valueOf((String) ex.innerResponse).build()
        }
        catch (EntityNotFoundException ex) {
            ProductErrorReason.PRODUCT_NOT_FOUND.build()
        }
    }

    CustomerOrdersResult findCustomerOrders(FindOrdersInput findOrdersInput) {
        try {
            orderBridge.findCustomerOrders(findOrdersInput)
        }
        catch (EntityNotFoundException ex) {
            CustomerOrderFindFailedReason.ORDER_NOT_FOUND.build()
        }
    }

    CustomerOrderResult findCustomerOrder(FindSupplierOrderInput findSupplierOrderInput) {
        try {
            orderBridge.findCustomerOrder(findSupplierOrderInput)
        }
        catch (EntityNotFoundException ex) {
            CustomerOrderFindFailedReason.ORDER_NOT_FOUND.build()
        }
    }

    CustomerSupplierOrderResult findCustomerAndSupplierOrders(FindCustomerAndSupplierOrdersInput findOrderAndSupplierOrderInput) {
        try {
            orderBridge.findCustomerAndSupplierOrders(findOrderAndSupplierOrderInput)
        }
        catch (EntityNotFoundException ex) {
            CustomerOrderFindFailedReason.ORDER_NOT_FOUND.build()
        }
    }

    PreSignedObject findCustomerLegalDocument(FindCustomerLegalDocumentInput findCustomerLegalDocumentInput) {
        customerBridge.findCustomerLegalDocument(findCustomerLegalDocumentInput)
    }


    List<Address> findAddresses(AccessTokenInput accessTokenInput) {
        customerBridge.findAddresses(accessTokenInput)
    }

    AddressResult getAddress(AddressIdInput addressIdInput) {
        try {
            customerBridge.getAddress(addressIdInput)
        }
        catch (EntityNotFoundException ex) {
            AddressFailedReason.ADDRESS_NOT_FOUND.build()
        }

    }

    AddressResult getPreferredAddress(AccessTokenInput accessTokenInput) {
        try {
            customerBridge.getPreferredAddress(accessTokenInput.accessToken)
        } catch (EntityNotFoundException ex) {
            AddressFailedReason.ADDRESS_NOT_FOUND.build()
        }
    }

    List<CustomerCancelOptionReason> getCancelOptions(AccessTokenInput accessTokenInput) {
        customerBridge.getCancelOptions(accessTokenInput.accessToken)
    }

    CartResult refreshCart(RefreshCartInput refreshCartInput) {
        if (refreshCartInput.products.size() == 0) {
            log.debug("refresh cart error: EMPTY_PRODUCTS")
            CartFailedReason.valueOf(CartFailedReason.EMPTY_PRODUCTS.name()).build()
        } else groceryListing.refreshCart(refreshCartInput.accessToken, refreshCartInput.products)
    }

    boolean validateUsername(ValidateUsernameInput input) {
        validationsBridge.validateUsername(input)
    }

    boolean validate(ValidateInput input) {
        try {
            return validationsBridge.validate(input)
        } catch (EntityNotFoundException ex) {
            false
        }
    }

    boolean customerHasOrders(AccessTokenInput accessTokenInput) {
        customerBridge.customerHasOrders(accessTokenInput)
    }

    Integer getPendingRatesCount(AccessTokenInput accessTokenInput) {
        customerBridge.getPendingRatesCount(accessTokenInput)
    }

    SupplierRatingsResponse getSupplierRatings(GetSupplierRatingsInput supplierRatingsInput) {
        customerBridge.getSupplierRatings(supplierRatingsInput.accessToken, supplierRatingsInput.supplierId, supplierRatingsInput.page, supplierRatingsInput.size)
    }

    List<SupplierOrder> getSupplierOrdersPendingToRate(AccessTokenInput accessTokenInput) {
        customerBridge.getSupplierOrdersPendingToRate(accessTokenInput.accessToken)
    }

    List<SupplierOrder> getSupplierOrders(GetSupplierOrdersInput input) {
        orderBridge.getSupplierOrders(input.accessToken, input.orderId)
    }

    SupplierResponse getSupplier(GetSupplierInput getSupplierInput) {
        try {
            productBridge.getSupplierById(getSupplierInput.accessToken, getSupplierInput.supplierId)
        } catch (EntityNotFoundException ex) {
            SupplierFailedReason.NOT_FOUND.build()
        }

    }


    SupplierOrderResponse getSupplierOrder(GetSupplierOrderInput supplierOrderInput) {
        try {
            orderBridge.getSupplierOrder(supplierOrderInput.accessToken, supplierOrderInput.supplierOrderId)
        } catch (EntityNotFoundException ex) {
            SupplierOrderFailedReason.NOT_FOUND.build()
        }
    }

    GetHomeBrandsResponse getHomeBrands(GetBrandsInput brandsInput) {
        groceryListing.getHomeBrands(brandsInput.accessToken, brandsInput.countryId)
    }

    GetHomeBrandsResponse previewHomeBrands(CoordinatesInput input) {
        groceryListing.getHomeBrands(input)
    }

    List<CountryConfigurationEntry> getCountryConfiguration(String countryId) {
        countryBridge.getCountryConfiguration(countryId)
    }

    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken) {
        countryBridge.getCustomerCountryConfiguration(accessToken)
    }

    List<Country> getHomeCountries(CountryHomeInput input) {
        countryBridge.getHomeCountries(input.locale)
    }

    Country getCountry(String countryId) {
        countryBridge.getCountry(countryId)
    }

    List<Category> findRootCategories(AccessTokenInput accessTokenInput) {
        categoryBridge.findRootCategories(accessTokenInput.accessToken)
    }

    RootCategoriesResponse previewRootCategories(CoordinatesInput coordinatesInput) {
        try {
            categoryBridge.previewRootCategories(coordinatesInput)
        }
        catch (BadRequestErrorException ex) {
            RootCategoriesFailedReasons.valueOf((String) ex.innerResponse).build()
        }

    }

    PromotionResponse getPromotions(PromotionInput promotionInput) {
        groceryListing.getPromotions(promotionInput)
    }

    PromotionResult previewPromotions(CoordinatesInput coordinatesInput) {
        groceryListing.getPromotions(coordinatesInput)
    }

    PromotionResult getLandingPromotion(GetLandingPromotionInput input) {
        try {
            promotionBridge.getLandingPromotion(input)
        }
        catch (EntityNotFoundException ex) {
            GetLandingPromotionFailedReason.NOT_FOUND.build()
        }
    }

    PromotionResult previewLandingPromotion(CoordinatesInput coordinatesInput) {
        try {
            promotionBridge.previewLandingPromotion(coordinatesInput)
        }
        catch (EntityNotFoundException ex) {
            GetLandingPromotionFailedReason.NOT_FOUND.build()
        }
        catch (BadRequestErrorException ex) {
            GetLandingPromotionFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    List<State> getStatesByCountry(String countryId) {
        stateBridge.getByCountryId(countryId)
    }

    SummaryResult getOrderPriceSummary(OrderSummaryInput orderSummaryInput) {
        try {
            orderBridge.getOrderSummary(orderSummaryInput.accessToken, orderSummaryInput.products, orderSummaryInput.wabiPayAccessToken, orderSummaryInput.coupons)
        }
        catch (BadRequestErrorException ex) {
            SummaryFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    BannerDialogResult getBannerDialog(String accessToken) {
        try {
            siteConfigurationBridge.getBannerDialog(accessToken)
        }
        catch (BadRequestErrorException ex) {
            SiteConfigurationFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    ValidateOrderResult validateOrder(ValidateOrderInput validateOrderInput) {
        try {
            orderBridge.validateOrder(validateOrderInput)
        }
        catch (BadRequestErrorException ex) {
            ValidateOrderFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    HomeSupplierResult previewHomeSuppliers(CoordinatesInput input) {
        groceryListing.previewHomeSuppliers(input)
    }

    List<SuppliersNameResult> getSuppliersThatHasSuggestedOrders(AccessTokenInput accessTokenInput) {
        customerBridge.getSuppliersThatHasSuggestedOrders(accessTokenInput.accessToken)
    }

    SuggestedOrderResult getSuggestedOrder(GetSuggestedOrderInput input) {
        customerBridge.getSuggestedOrder(input)
    }

    List<SupplierOrder> findPendingRateSinceLastLogin(AccessTokenInput input) {
        customerBridge.findPendingRateSinceLastLogin(input.accessToken)
    }

    Boolean isValidPhone(IsValidPhoneInput input) {
        phoneNotifierBridge.isValidPhone(input.countryCode, input.phone)
    }

    Boolean isCountryCodeAndPhoneAvailable(IsPhoneAvailableInput input) {
        authServerBridge.isCountryCodeAndPhoneAvailable(input.countryCode, input.phone, input.accessToken)
    }

}


