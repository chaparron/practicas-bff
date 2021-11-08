package bff.resolver

import bff.bridge.SupplierBridge
import bff.model.Supplier
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class SupplierResolver implements GraphQLResolver<Supplier> {

    @Autowired
    SupplierBridge supplierBridge

    String averageDeliveryDay(Supplier supplier) {
        return supplier.averageDeliveryDay?:supplierBridge.getAverageDeliveryDays(supplier.accessToken, supplier.id)
    }
}
