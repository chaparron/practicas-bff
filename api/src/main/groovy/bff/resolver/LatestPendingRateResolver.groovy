package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.LatestPendingRates
import bff.model.Supplier
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LatestPendingRateResolver implements GraphQLResolver<LatestPendingRates>{

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    Supplier supplier(LatestPendingRates latestPendingRates) {
        supplierOrderBridge.getSupplierBySupplierOrderId(latestPendingRates.accessToken, latestPendingRates.supplierOrderId)
    }
}
