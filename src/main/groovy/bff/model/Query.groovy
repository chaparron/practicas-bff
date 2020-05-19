package bff.model

import bff.bridge.*
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

    ResendVerifySMSResult resendVerifySMS(AccessTokenInput accessTokenInput) {
        try {
            customerBridge.resendVerifySMS(accessTokenInput)
            Void.SUCCESS
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    ProductResult productDetail(ProductInput productInput) {
        try {
            productBridge.getProductById(productInput.accessToken, productInput.productId)
        }
        catch (BadRequestErrorException ex) {
            ProductErrorReason.valueOf((String) ex.innerResponse).build()
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
        try {
            productBridge.refreshCart(refreshCartInput.accessToken, refreshCartInput.products)
        } catch (BadRequestErrorException ex) {
            CartFailedReason.valueOf((String) ex.innerResponse).build()
        }

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
        try {
            brandBridge.getHome(brandsInput.accessToken, brandsInput.countryId)
        }
        catch (EntityNotFoundException ex) {
            GetBrandsFailedReason.NOT_FOUND.build()
        }
        catch (BadRequestErrorException ex) {
            GetBrandsFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    GetHomeBrandsResponse previewHomeBrands(CoordinatesInput coordinatesInput) {
        try {
            brandBridge.previewHomeBrands(coordinatesInput)
        }
        catch (EntityNotFoundException ex) {
            GetBrandsFailedReason.NOT_FOUND.build()
        }
        catch (BadRequestErrorException ex) {
            GetBrandsFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    List<CountryConfigurationEntry> getCountryConfiguration(String countryId) {
        countryBridge.getCountryConfiguration(countryId)
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
        promotionBridge.getAll(promotionInput)
    }

    PromotionResult getLandingPromotion(GetLandingPromotionInput input) {
        try {
            promotionBridge.getLandingPromotion(input)
        }
        catch (EntityNotFoundException ex) {
            GetLandingPromotionFailedReason.NOT_FOUND.build()
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
        try
        {
            siteConfigurationBridge.getBannerDialog(accessToken)
        }
        catch (BadRequestErrorException ex) {
            SiteConfigurationFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }
}

