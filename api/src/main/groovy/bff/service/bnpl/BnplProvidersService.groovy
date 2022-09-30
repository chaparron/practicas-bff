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

import static bff.model.CreditLineProvider.buildSuperMoneyCreditLineProvider

@Slf4j()
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
                .nextCondition { bnplBridge.customerStatus(accessToken).active }
                .nextCondition { supplierHasBnplWallet(Collections.singletonList(supplier), accessToken, supplier.id.toString()) }
                .successfullyValue([buildSuperMoneyCreditLineProvider()])
                .unsuccessfullyValue(null)
                .execute()
    }

    List<CreditLineProvider> creditLineProvidersFor(SupplierOrder supplierOrder) {
        if (supplierOrder.order == null) {
            log.error("Not enough information for order in supplierOrder: {}", supplierOrder)
            return null
        }

        def order = supplierOrder.order
        def supplierOrders = order.supplierOrders
        def suppliers = supplierOrders.collect { supplierOrderBridge.getSupplierBySupplierOrderId(it.accessToken, it.id) }
        def accessToken = supplierOrder.accessToken
        def supplierId = supplierOrderBridge.getSupplierBySupplierOrderId(accessToken, supplierOrder.id).id.toString()
        def country = JwtToken.countryFromString(accessToken)

        new BnplCreditLineProvidersProcess()
                .nextCondition { enabledCountries.contains(country) }
                .nextCondition { [OrderStatus.PENDING, OrderStatus.IN_PROGRESS].contains(order.status) }
                .nextCondition { supplierOrder.payment_pending >= bnplBridge.supportedMinimumAmount(country, accessToken).amount }
                .nextCondition { bnplBridge.customerStatus(accessToken).active }
                .nextCondition { supplierHasBnplWallet(suppliers, accessToken, supplierId) }
                .successfullyValue([buildSuperMoneyCreditLineProvider()])
                .unsuccessfullyValue(null)
                .execute()
    }

    private boolean supplierHasBnplWallet(List<Supplier> suppliers, String accessToken, String supplierId) {
        def suppliersId = suppliers.stream().map { it.id.toString() }.collect(Collectors.toList())
        log.debug("About to find BNPL wallet for suppliers {}", suppliersId)
        def userId = JwtToken.userIdFromToken(accessToken)

        walletBridge.getSupportedProvidersBetween(suppliersId, userId, WalletProvider.@Companion.buyNowPayLater(), accessToken)
                .supplierProviders.any { it -> it.supplierId == supplierId }
    }
}
