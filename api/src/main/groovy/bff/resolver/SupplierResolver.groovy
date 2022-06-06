package bff.resolver

import bff.bridge.SupplierBridge
import bff.model.Supplier
import bff.model.SupplierAvatarSize
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

@Component
class SupplierResolver implements GraphQLResolver<Supplier> {

    @Autowired
    SupplierBridge supplierBridge

    String avatar(Supplier supplier, SupplierAvatarSize size) {
        ofNullable(supplier.avatar)
                .map { avatar ->
                    ofNullable(size).map { imageService.url(avatar, it) }.orElse(avatar)
                }
                .orElse(null)
    }

    String averageDeliveryDay(Supplier supplier) {
        return supplier.averageDeliveryDay != null ?
                supplier.averageDeliveryDay.orElse(null) :
                supplierBridge.getAverageDeliveryDays(supplier.accessToken, supplier.id)
    }


}
