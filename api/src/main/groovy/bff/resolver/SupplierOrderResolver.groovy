package bff.resolver

import bff.bridge.PaymentsBridge
import bff.bridge.DigitalPaymentsBridge
import bff.bridge.SupplierOrderBridge
import bff.model.*
import bff.service.MoneyService
import bff.service.bnpl.BnplProvidersService
import com.coxautodev.graphql.tools.GraphQLResolver
import digitalpayments.sdk.model.Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import wabi2b.payments.common.model.request.GetSupplierOrderPaymentRequest
import wabi2b.payments.common.model.response.GetSupplierOrderPaymentResponse

import static java.util.Optional.ofNullable

@Component
class SupplierOrderResolver implements GraphQLResolver<SupplierOrder> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    @Autowired
    MoneyService moneyService

    @Autowired
    BnplProvidersService bnplProvidersService

    @Autowired
    private DigitalPaymentsBridge digitalPaymentsBridge

    @Autowired
    private PaymentsBridge paymentsBridge

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
        def digitalPaymentProviders = digitalPaymentsBridge.getPaymentProviders(supplier.id.toString(), supplierOrder.accessToken).block()

        def isJPMorganSupported = digitalPaymentProviders.any {it == Provider.JP_MORGAN}

        List<SupportedPaymentProvider> result = []

        if (isJPMorganSupported) {
            result.add(new JPMorganPaymentProvider())
        }

        if(ofNullable(creditLineProviders(supplierOrder)).map {!it.isEmpty()}.orElse(false)) {
            result.add(new SupermoneyPaymentProvider())
        }

        return result
    }

    SimpleTextButton paymentButton(SupplierOrder supplierOrder) {
        Mono.just(supplierOrder).filter {it.isPayable() }.<GetSupplierOrderPaymentResponse>zipWhen {
            paymentsBridge.getSupplierOrderPayments(new GetSupplierOrderPaymentRequest(supplierOrder.id), supplierOrder.accessToken)
        }.map {
            SimpleTextButtonBuilder.buildFrom(supportedPaymentProviders(it.t1), it.t2)
        }.defaultIfEmpty(SimpleTextButton.hidden()).block()
    }

    List<SupplierOrderPaymentV2> payments(SupplierOrder supplierOrder) {
        paymentsBridge.getSupplierOrderPayments(new GetSupplierOrderPaymentRequest(supplierOrder.id), supplierOrder.accessToken).map { response ->
            response.payments.collect {it ->
                new SupplierOrderPaymentV2(
                        supplierOrderId: supplierOrder.id,
                        paymentId: it.paymentId,
                        paymentData: PaymentDataBuilder.buildFrom(it.paymentMethod)
                )
            }
        }.block()
    }

    BigDecimal defaultPaymentAmount(SupplierOrder supplierOrder) {
        def request = new GetSupplierOrderPaymentRequest(supplierOrder.id)
        def response = paymentsBridge.getSupplierOrderPayments(request, supplierOrder.accessToken).block()
        response.totalAmount - response.lockedAmount
    }
}

abstract class SimpleTextButtonBuilder {

    protected GetSupplierOrderPaymentResponse response
    protected List<SupportedPaymentProvider> providers

    SimpleTextButtonBuilder(List<SupportedPaymentProvider> providers, GetSupplierOrderPaymentResponse response) {
        this.response = response
        this.providers = providers
    }

    static SimpleTextButton buildFrom(List<SupportedPaymentProvider> providers, GetSupplierOrderPaymentResponse response) {
        return [new JPMCSimpleTextButtonBuilder(providers, response), new SuperMoneySimpleTextButtonBuilder(providers, response)].find {
            it.isSupported()
        }?.build() ?: SimpleTextButton.hidden()
    }

    abstract SimpleTextButtonBehavior behavior()
    abstract boolean isSupported()

    protected SimpleTextButton build() {
        def textKey = isFull() ? PaymentStatus.TOTALLY_PAID.name() : paymentStatus().name()
        return new SimpleTextButton(behavior(), textKey)
    }

    protected Boolean isFull() {
        response.totalAmount == response.lockedAmount
    }

    private PaymentStatus paymentStatus() {
        !response.payments.isEmpty() ? PaymentStatus.PARTIALLY_PAID : PaymentStatus.UNPAID
    }
}

class SuperMoneySimpleTextButtonBuilder extends SimpleTextButtonBuilder {

    SuperMoneySimpleTextButtonBuilder(List<SupportedPaymentProvider> providers, GetSupplierOrderPaymentResponse payments) {
        super(providers, payments)
    }

    @Override
    SimpleTextButtonBehavior behavior() {
        response.payments.isEmpty() ? SimpleTextButtonBehavior.VISIBLE : SimpleTextButtonBehavior.HIDDEN
    }

    @Override
    boolean isSupported() {
        providers
                .any {
                    it.getClassName() == SupermoneyPaymentProvider.class.simpleName
                }
    }
}

class JPMCSimpleTextButtonBuilder extends SimpleTextButtonBuilder {

    JPMCSimpleTextButtonBuilder(List<SupportedPaymentProvider> providers, GetSupplierOrderPaymentResponse payments) {
        super(providers, payments)
    }

    @Override
    SimpleTextButtonBehavior behavior() {
        !isFull() ? SimpleTextButtonBehavior.VISIBLE : SimpleTextButtonBehavior.HIDDEN
    }

    @Override
    boolean isSupported() {
        providers
                .any {
                    it.getClassName() == JPMorganPaymentProvider.class.simpleName
                }
    }
}

abstract class PaymentDataBuilder {

    protected wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod

    PaymentDataBuilder(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod
    }

    static PaymentData buildFrom(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        return [
                new NetBankingBuilder(paymentMethod),
                new UPIBuilder(paymentMethod),
                new DigitalWalletBuilder(paymentMethod),
                new CreditCardBuilder(paymentMethod),
                new DebitCardBuilder(paymentMethod),
                new DefaultPaymentMethodBuilder(paymentMethod),
                new BuyNowPayLaterPaymentMethodBuilder(paymentMethod)
        ].find { it.isSupported() }.doBuild()

    }

    protected abstract Boolean isSupported()
    protected abstract PaymentMethod paymentMethod()
    protected abstract PaymentData doBuild()

    protected DigitalPaymentPaymentData buildForDigitalPayment() {
        new DigitalPaymentPaymentData(
                paymentMethod: paymentMethod()
        )
    }
}

class NetBankingBuilder extends PaymentDataBuilder {

    NetBankingBuilder(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        super(paymentMethod)
    }

    @Override
    protected Boolean isSupported() {
        paymentMethod == wabi2b.payments.common.model.dto.type.PaymentMethod.NET_BANKING
    }

    @Override
    protected PaymentMethod paymentMethod() {
        new NetBanking()
    }

    @Override
    protected PaymentData doBuild() {
        buildForDigitalPayment()
    }
}

class UPIBuilder extends PaymentDataBuilder {

    UPIBuilder(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        super(paymentMethod)
    }

    @Override
    protected Boolean isSupported() {
        paymentMethod == wabi2b.payments.common.model.dto.type.PaymentMethod.UPI
    }

    @Override
    protected PaymentMethod paymentMethod() {
        new UPI()
    }

    @Override
    protected PaymentData doBuild() {
        buildForDigitalPayment()
    }
}

class CreditCardBuilder extends PaymentDataBuilder {

    CreditCardBuilder(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        super(paymentMethod)
    }

    @Override
    protected Boolean isSupported() {
        paymentMethod == wabi2b.payments.common.model.dto.type.PaymentMethod.CREDIT_CARD
    }

    @Override
    protected PaymentMethod paymentMethod() {
        new CreditCard()
    }

    @Override
    protected PaymentData doBuild() {
        buildForDigitalPayment()
    }
}

class DebitCardBuilder extends  PaymentDataBuilder {

    DebitCardBuilder(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        super(paymentMethod)
    }

    @Override
    protected Boolean isSupported() {
        paymentMethod == wabi2b.payments.common.model.dto.type.PaymentMethod.DEBIT_CARD
    }

    @Override
    protected PaymentMethod paymentMethod() {
        new DebitCard()
    }

    @Override
    protected PaymentData doBuild() {
        buildForDigitalPayment()
    }
}

class DigitalWalletBuilder extends PaymentDataBuilder {

    DigitalWalletBuilder(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        super(paymentMethod)
    }

    @Override
    protected Boolean isSupported() {
        paymentMethod == wabi2b.payments.common.model.dto.type.PaymentMethod.DIGITAL_WALLET
    }

    @Override
    protected PaymentMethod paymentMethod() {
        new NetBanking()
    }

    @Override
    protected PaymentData doBuild() {
        buildForDigitalPayment()
    }
}

class BuyNowPayLaterPaymentMethodBuilder extends  PaymentDataBuilder {

    BuyNowPayLaterPaymentMethodBuilder(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        super(paymentMethod)
    }

    @Override
    protected Boolean isSupported() {
        paymentMethod == wabi2b.payments.common.model.dto.type.PaymentMethod.BUY_NOW_PAY_LATER
    }

    @Override
    protected PaymentMethod paymentMethod() {
        new BuyNowPayLaterPaymentMethod()
    }

    @Override
    protected PaymentData doBuild() {
        new BuyNowPayLaterPaymentData(
                paymentMethod: paymentMethod()
        )
    }
}

class DefaultPaymentMethodBuilder extends PaymentDataBuilder {

    DefaultPaymentMethodBuilder(wabi2b.payments.common.model.dto.type.PaymentMethod paymentMethod) {
        super(paymentMethod)
    }

    @Override
    protected Boolean isSupported() {
        true
    }

    @Override
    protected PaymentMethod paymentMethod() {
        new DefaultPaymentMethod()
    }

    @Override
    protected PaymentData doBuild() {
        buildForDigitalPayment()
    }
}

