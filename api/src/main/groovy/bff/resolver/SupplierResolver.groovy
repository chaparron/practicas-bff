package bff.resolver

import bff.bridge.SupplierBridge
import bff.model.PreviewSupplier
import bff.model.Supplier
import bff.model.SupplierAvatarSize
import bff.model.utils.DfeUtils
import com.coxautodev.graphql.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable
import static java.util.Optional.ofNullable


@Component
class SupplierResolver implements GraphQLResolver<Supplier> {

    @Autowired
    SupplierBridge supplierBridge

    private final String REFRESH_CART_QRY = "refreshCart"

    String avatar(Supplier supplier, SupplierAvatarSize size) {
        ofNullable(supplier.avatar)
                .map { avatar ->
                    ofNullable(size).map { imageService.url(avatar, it) }.orElse(avatar)
                }
                .orElse(null)
    }

    String averageDeliveryDay(Supplier supplier, DataFetchingEnvironment dfe) {
        String accessToken = supplier.accessToken
        return supplier.averageDeliveryDay || !DfeUtils.isOperation(dfe, REFRESH_CART_QRY ) ?
                supplier.averageDeliveryDay
                : supplierBridge.getAverageDeliveryDays(accessToken, supplier.id)
    }


}
