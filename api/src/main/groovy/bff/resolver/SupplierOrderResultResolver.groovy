package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.Money
import bff.model.RatingEntry
import bff.model.RatingOwner
import bff.model.SupplierOrderResult
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierOrderResultResolver implements GraphQLResolver<SupplierOrderResult> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    @Autowired
    MoneyService moneyService

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

    Money deliveryCostMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.deliveryCost)
    }

    Money totalMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.total)
    }

    Money subTotalMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.subTotal)
    }

    Money creditsPaidMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.credits_paid)
    }

    Money moneyPaidMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.money_paid)
    }

    Money paymentPendingMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.payment_pending)
    }

    Money totalWabipayMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.total_wabipay)
    }

    Money serviceFeeMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.service_fee)
    }

    Money discountsMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.discounts)
    }

    Money localTaxesMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.localTaxes)
    }

    Money discountUsedMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.discount_used)
    }

    Money amountMoney(SupplierOrderResult supplierOrderRes) {
        moneyService.getMoney(supplierOrderRes.accessToken, supplierOrderRes.amount)
    }

}
