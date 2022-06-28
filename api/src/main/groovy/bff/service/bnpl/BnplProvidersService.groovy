package bff.service.bnpl

import bff.JwtToken
import bff.model.CreditLineProvider
import bff.model.CreditProvider
import bff.model.Order
import bff.model.OrderStatus
import bff.model.Supplier
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

    List<CreditLineProvider> creditLineProvidersFor(Supplier supplier, String accessToken) {
        def country = JwtToken.countryFromString(accessToken)

        new BnplCreditLineProvidersProcess()
                .nextCondition { enabledCountries.contains(country) }
                .nextCondition { currentUserHasBnplWallet(accessToken) }
                .nextCondition { supplierHasBnplWallet(supplier) }
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
                .nextCondition { supplierHasBnplWallet(supplier) }
                .successfullyValue([new CreditLineProvider(provider: CreditProvider.SUPERMONEY)])
                .unsuccessfullyValue(null)
                .execute()
    }

    private boolean currentUserHasBnplWallet(String accessToken) {
        log.debug("About to find BNPL wallet for user {}", JwtToken.userIdFromToken(accessToken))

        walletSdk
                .getWallet(WalletProvider.@Companion.buyNowPayLater(), accessToken)
                .blockOptional()
                .isPresent()
    }

    private boolean supplierHasBnplWallet(Supplier supplier) {
        log.debug("About to find BNPL wallet for supplier {}", supplier.id)

        //TODO: missing sdk method

        true
    }
}
