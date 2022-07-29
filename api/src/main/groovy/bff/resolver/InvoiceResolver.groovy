package bff.resolver

import bff.bridge.CustomerBridge
import bff.model.InvoicesResponse
import bff.model.RetailerInfoSummary
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class InvoiceResolver implements GraphQLResolver<InvoicesResponse> {
    @Autowired
    CustomerBridge customerBridge

    RetailerInfoSummary retailerInfoSummary(InvoicesResponse invoicesResponse) {
        customerBridge.retailerInfoSummary(invoicesResponse.accessToken, invoicesResponse.from, invoicesResponse.to)
    }

}
