package bff.model


import bff.bridge.AuthServerBridge
import bff.bridge.CustomerBridge
import bff.bridge.OrderBridge
import bff.bridge.ProductBridge
import bff.configuration.AccessToBackendDeniedException
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

    Void testPhoneNumber(PhoneInput phoneInput) {
        authServerBridge.testPhoneNumber(phoneInput.phone)
        Void.SUCCESS
    }

    Customer myProfile(CustomerInput customerInput) {
        customerBridge.myProfile(customerInput)
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

    CustomerOrdersResult findCustomerOrders(FindOrdersInput findOrdersInput) {
        try {
            orderBridge.findCustomerOrders(findOrdersInput)
        }
        catch (EntityNotFoundException ex) {
            CustomerOrderFindFailedReason.ORDER_NOT_FOUND.build()
        }
    }

    List<Address> findAddresses(AccessTokenInput accessTokenInput) {
        customerBridge.findAddresses(accessTokenInput)
    }

    CartResult refreshCart(RefreshCartInput refreshCartInput) {
        try {
            productBridge.refreshCart(refreshCartInput.accessToken, refreshCartInput.products)
        } catch (BadRequestErrorException ex) {
            CartFailedReason.valueOf((String) ex.innerResponse).build()
        } catch (AccessToBackendDeniedException ex) {
            CartFailedReason.FORBIDDEN.build()
        }

    }
}

