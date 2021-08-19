package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.RatingEntry
import bff.model.RatingOwner
import bff.model.SupplierOrderResult
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierOrderResultResolver implements GraphQLResolver<SupplierOrderResult> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge


    RatingEntry rating(SupplierOrderResult supplierOrderRes) {
        if (!supplierOrderRes.rating && supplierOrderRes.ratings && supplierOrderRes.ratings.size() > 0) {
            def supplierRating = supplierOrderRes.ratings.get(RatingOwner.SUPPLIER)
            supplierRating?.accessToken = supplierOrderRes.accessToken

            def customerRating = supplierOrderRes.ratings.get(RatingOwner.CUSTOMER)
            customerRating?.accessToken = supplierOrderRes.accessToken

            return new RatingEntry(
                    SUPPLIER: supplierRating,
                    CUSTOMER: customerRating
            )
        }
        supplierOrderRes.rating?:supplierOrderBridge.getRatingBySupplierOrderId(supplierOrderRes.accessToken, supplierOrderRes.id)
    }


}
