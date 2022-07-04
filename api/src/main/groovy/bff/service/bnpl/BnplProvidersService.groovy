package bff.service.bnpl

import bff.JwtToken
import bff.bridge.SupplierOrderBridge
import bff.model.*
import bnpl.sdk.BnPlSdk
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.sdk.client.WalletSdk

import static java.util.Collections.singletonList

@Slf4j
@Service
class BnplProvidersService {
    @Autowired
    private SupplierOrderBridge supplierOrderBridge

    @Value('${bnpl.enabled.countries:[]}')
    private List<String> enabledCountries

    @Autowired
    private WalletSdk walletSdk

    @Autowired
    private BnPlSdk bnPlSdk

    List<CreditLineProvider> creditLineProvidersFor(OrderSummary os, Money total) {
        def supplier = os.supplier
        def accessToken = os.supplier.accessToken
        def country = JwtToken.countryFromString(accessToken)

        new BnplCreditLineProvidersProcess()
                .nextCondition { enabledCountries.contains(country) }
                .nextCondition { total.amount >= bnPlSdk.supportedLimitedAmount(country, accessToken).block().amount }
                .nextCondition { currentUserHasBnplWallet(accessToken) }
                .nextCondition { supplierHasBnplWallet(supplier, accessToken) }
                .successfullyValue([new CreditLineProvider(provider: CreditProvider.SUPERMONEY)])
                .unsuccessfullyValue(null)
                .execute()
    }

    List<CreditLineProvider> creditLineProvidersFor(SupplierOrder supplierOrder) {
        def supplier = supplierOrderBridge.getSupplierBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
        def order = supplierOrderBridge.getOrderBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
        def accessToken = supplierOrder.accessToken
        def country = JwtToken.countryFromString(accessToken)

        new BnplCreditLineProvidersProcess()
                .nextCondition { enabledCountries.contains(country) }
                .nextCondition { [OrderStatus.PENDING, OrderStatus.IN_PROGRESS].contains(order.status) }
                .nextCondition { supplierOrder.payment_pending >= bnPlSdk.supportedLimitedAmount(country, accessToken).block().amount }
                .nextCondition { currentUserHasBnplWallet(accessToken) }
                .nextCondition { supplierHasBnplWallet(supplier, accessToken) }
                .successfullyValue([new CreditLineProvider(provider: CreditProvider.SUPERMONEY)])
                .unsuccessfullyValue(null)
                .execute()
    }

    private boolean currentUserHasBnplWallet(String accessToken) {
        log.debug("About to find BNPL wallet for user {}", JwtToken.userIdFromToken(accessToken))

        walletSdk
                .getWallet(WalletProvider.@Companion.buyNowPayLater(), accessToken) != null
    }

    private boolean supplierHasBnplWallet(Supplier supplier, String accessToken) {
        log.debug("About to find BNPL wallet for supplier {}", supplier.id)
        def userId = JwtToken.userIdFromToken(accessToken)
        def suppliers = singletonList(supplier.id.toString())

        walletSdk.getSupportedProvidersBetween(suppliers, userId, WalletProvider.@Companion.buyNowPayLater(), accessToken).supplierProviders.any { it -> it.supplierId.toLong() == supplier.id }
    }
}
