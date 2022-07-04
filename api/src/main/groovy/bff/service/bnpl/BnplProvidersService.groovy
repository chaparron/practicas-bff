package bff.service.bnpl

import bff.JwtToken
import bff.model.CreditLineProvider
import bff.model.CreditProvider
import bff.model.Money
import bff.model.Order
import bff.model.OrderStatus
import bff.model.OrderSummary
import bff.model.Supplier
import bnpl.sdk.BnPlSdk
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.sdk.client.WalletSdk

@Slf4j
@Service
class BnplProvidersService {

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
                .nextCondition { total.amount <= bnPlSdk.supportedLimitedAmount(country, accessToken).block().amount }
                .nextCondition { currentUserHasBnplWallet(accessToken) }
                .nextCondition { supplierHasBnplWallet(supplier, accessToken) }
                .successfullyValue([new CreditLineProvider(provider: CreditProvider.SUPERMONEY)])
                .unsuccessfullyValue(null)
                .execute()
    }

    List<CreditLineProvider> creditLineProvidersFor(Supplier supplier, Order order, String accessToken) {
        def country = JwtToken.countryFromString(accessToken)

        new BnplCreditLineProvidersProcess()
                .nextCondition { enabledCountries.contains(country) }
                .nextCondition { [OrderStatus.PENDING, OrderStatus.IN_PROGRESS].contains(order.status) }
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

        !walletSdk.getSupportedProvidersBetween([supplier.id.toString()], userId, WalletProvider.@Companion.buyNowPayLater(), accessToken).supplierProviders.isEmpty()
    }
}
