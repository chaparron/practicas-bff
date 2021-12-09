package bff.model

import bff.bridge.sdk.credits.CreditService
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class CreditsQuery implements GraphQLQueryResolver {

    @Autowired
    CreditService creditServiceService

    wabi2b.sdk.credits.PageResponse findSupplierCreditBalances(FindSupplierCreditBalancesInput input) {
        return creditServiceService.findSupplierCreditBalances(input)
    }
}
