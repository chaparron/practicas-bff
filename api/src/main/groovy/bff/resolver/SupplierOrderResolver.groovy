package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.*
import bff.service.MoneyService
import bff.service.bnpl.BnplProvidersService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static bff.model.CreditLineProvider.buildSuperMoneyCreditLineProvider

@Component
class SupplierOrderResolver implements GraphQLResolver<SupplierOrder> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    @Autowired
    MoneyService moneyService

    @Autowired
    BnplProvidersService bnplProvidersService

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

    SupportedPaymentProviders supportedPaymentProviders(SupplierOrder supplierOrder) {
        switch (supplierOrder.total.toInteger()) {
            case 0..100:
                createSupportedPaymentProviders(PaymentType.DIGITAL)
                break
            case 101..200:
                createSupportedPaymentProviders(PaymentType.SUPERMONEY)
                break
            case 201..300:
                createSupportedPaymentProviders(PaymentType.BOTH)
                break
            default:
                new SupportedPaymentProviders(providers: [], paymentMode: new PaymentMode(paymentType: PaymentModeType.NONE))
                break
        }
    }

    private static SupportedPaymentProviders createSupportedPaymentProviders(PaymentType dummyType) {
        switch (dummyType) {
            case PaymentType.DIGITAL: return new SupportedPaymentProviders(
                    providers: [digitalSupportedPaymentPaymentProvider()],
                    paymentMode: new PaymentMode(paymentType: PaymentModeType.PAY_NOW))
            case PaymentType.SUPERMONEY: return new SupportedPaymentProviders(
                    providers: [superMoneySupportedPaymentPaymentProvider()],
                    paymentMode: new PaymentMode(paymentType: PaymentModeType.PAY_LATER))
            case PaymentType.BOTH: return new SupportedPaymentProviders(
                    providers: [superMoneySupportedPaymentPaymentProvider(), digitalSupportedPaymentPaymentProvider()],
                    paymentMode: new PaymentMode(paymentType: PaymentModeType.PAY_NOW_OR_LATER))
        }
    }
    private static digitalSupportedPaymentPaymentProvider() {
        return new SupportedPaymentProvider(
                title: PaymentProviderCode.JPMORGAN.name(),
                description: "desc",
                paymentProvider: new InstantPaymentProvider(providerCode: PaymentProviderCode.JPMORGAN),
                avatar: "69fbbf54-a2f4-441f-9f58-37f903a4e558.jpg")
    }
    private static superMoneySupportedPaymentPaymentProvider() {
        return new SupportedPaymentProvider(
                title: CreditProvider.SUPERMONEY.name(),
                description: "desc",
                paymentProvider: buildSuperMoneyCreditLineProvider(),
                avatar: "f3b62099-10e3-4b94-bb5a-8401cb49f88d.png")
    }

    // TODO Delete when real implementation replaces the mock
    private enum PaymentType {
        DIGITAL, SUPERMONEY, BOTH
    }

}

