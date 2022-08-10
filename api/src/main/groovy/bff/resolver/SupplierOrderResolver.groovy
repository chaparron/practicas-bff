package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.*
import bff.service.MoneyService
import bff.service.bnpl.BnplProvidersService
import com.coxautodev.graphql.tools.GraphQLResolver
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierOrderResolver implements GraphQLResolver<SupplierOrder> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    @Autowired
    MoneyService moneyService

    @Autowired
    BnplProvidersService bnplProvidersService

    @Autowired
    private DigitalPaymentsSdk digitalPaymentsSdk

    Supplier supplier(SupplierOrder supplierOrder) {
        supplierOrderBridge.getSupplierBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    List<OrderItem> products(SupplierOrder supplierOrder) {
        supplierOrderBridge.getOrderItemsBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    RatingEntry rating(SupplierOrder supplierOrder) {
        if (!supplierOrder.rating && supplierOrder.ratings && supplierOrder.ratings.size() > 0) {
            def supplierRating = supplierOrder.ratings.get(RatingOwner.SUPPLIER)
            supplierRating?.accessToken = supplierOrder.accessToken

            def customerRating = supplierOrder.ratings.get(RatingOwner.CUSTOMER)
            customerRating?.accessToken = supplierOrder.accessToken

            return new RatingEntry(
                    SUPPLIER: supplierRating,
                    CUSTOMER: customerRating
            )
        }
        supplierOrder.rating?:supplierOrderBridge.getRatingBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    Order order(SupplierOrder supplierOrder) {
        supplierOrderBridge.getOrderBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    List<AppliedPromotionResponse> appliedPromotions(SupplierOrder supplierOrder) {
        supplierOrderBridge.getPromotionsBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    Money deliveryCostMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.deliveryCost)
    }

    Money totalMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.total)
    }

    Money subTotalMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.subTotal)
    }

    Money creditsPaidMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.credits_paid)
    }

    Money moneyPaidMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.money_paid)
    }

    Money paymentPendingMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.payment_pending)
    }

    Money totalWabipayMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.total_wabipay)
    }

    Money serviceFeeMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.service_fee)
    }

    Money discountsMoney(SupplierOrder supplierOrder) {
        moneyService.getMoney(supplierOrder.accessToken, supplierOrder.discounts)
    }

    List<CreditLineProvider> creditLineProviders(SupplierOrder supplierOrder) {
        bnplProvidersService.creditLineProvidersFor(supplierOrder)
    }

    List<SupportedPaymentProvider> supportedPaymentProviders(SupplierOrder supplierOrder) {

        def supplier = supplierOrderBridge.getSupplierBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
        def digitalPaymentProviders = digitalPaymentsSdk.getPaymentProviders(supplier.id.toString(), supplierOrder.accessToken).block()

        def isJPMorganSupported = digitalPaymentProviders.any {it == Provider.JP_MORGAN}

        List<SupportedPaymentProvider> result = []

        if (isJPMorganSupported) {
            result.add(SupportedPaymentProvider.jpmMorganBuild())
        }

        if(!bnplProvidersService.creditLineProvidersFor(supplierOrder).isEmpty()) {
            result.add(SupportedPaymentProvider.supermoneyBuild())
        }

        return result
    }

    PaymentButton paymentButton(SupplierOrder supplierOrder) {

        new PaymentButton(true)
    }
}

