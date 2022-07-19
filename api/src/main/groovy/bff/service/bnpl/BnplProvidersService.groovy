package bff.service.bnpl

import bff.JwtToken
import bff.bridge.BnplBridge
import bff.bridge.OrderBridge
import bff.bridge.SupplierOrderBridge
import bff.bridge.WalletBridge
import bff.model.*
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import wabi2b.payments.common.model.request.WalletProvider

import java.util.stream.Collectors

import static java.util.Collections.singletonList

@Slf4j
@Service
class BnplProvidersService {

    @Autowired
    private SupplierOrderBridge supplierOrderBridge
    @Autowired
    private OrderBridge orderBridge

    @Value('${bnpl.enabled.countries:[]}')
    private List<String> enabledCountries

    @Autowired
    private WalletBridge walletBridge

    @Autowired
    private BnplBridge bnplBridge

    List<CreditLineProvider> creditLineProvidersFor(OrderSummary os, Money total) {
        def supplier = os.supplier
        def accessToken = os.supplier.accessToken
        def country = JwtToken.countryFromString(accessToken)

        new BnplCreditLineProvidersProcess()
                .nextCondition { enabledCountries.contains(country) }
                .nextCondition { total.amount >= bnplBridge.supportedMinimumAmount(country, accessToken).amount }
                .nextCondition { currentUserHasBnplWallet(accessToken) }
                .nextCondition { supplierHasBnplWallet(singletonList(supplier), accessToken) }
                .successfullyValue([new CreditLineProvider(provider: CreditProvider.SUPERMONEY)])
                .unsuccessfullyValue(null)
                .execute()
    }

    List<CreditLineProvider> creditLineProvidersFor(SupplierOrder supplierOrder) {
        def order = supplierOrder.order
        def supplierOrders = order.supplierOrders
        def suppliers = supplierOrders.collect { supplierOrderBridge.getSupplierBySupplierOrderId(it.accessToken, it.id) }
        def accessToken = supplierOrder.accessToken
        def country = JwtToken.countryFromString(accessToken)

        new BnplCreditLineProvidersProcess()
                .nextCondition { enabledCountries.contains(country) }
                .nextCondition { [OrderStatus.PENDING, OrderStatus.IN_PROGRESS].contains(order.status) }
                .nextCondition { supplierOrder.payment_pending >= bnplBridge.supportedMinimumAmount(country, accessToken).amount }
                .nextCondition { currentUserHasBnplWallet(accessToken) }
                .nextCondition { supplierHasBnplWallet(suppliers, accessToken) }
                .successfullyValue([new CreditLineProvider(provider: CreditProvider.SUPERMONEY)])
                .unsuccessfullyValue(null)
                .execute()
    }

    boolean currentUserHasBnplWallet(String accessToken) {
        def userId = JwtToken.userIdFromToken(accessToken)
        log.debug("About to find BNPL wallet for user {}", userId)

        walletBridge
                .getWallet(userId.toLong(), WalletProvider.@Companion.buyNowPayLater(), accessToken) != null
    }

    private boolean supplierHasBnplWallet(List<Supplier> suppliers, String accessToken) {
        def suppliersId = suppliers.stream().map {it.id.toString() }.collect(Collectors.toList())
        log.debug("About to find BNPL wallet for suppliers {}", suppliersId)
        def userId = JwtToken.userIdFromToken(accessToken)

        walletBridge.getSupportedProvidersBetween(suppliersId, userId, WalletProvider.@Companion.buyNowPayLater(), accessToken)
                .supplierProviders.any { it -> suppliersId.contains(it.supplierId.toString()) }
    }
}
