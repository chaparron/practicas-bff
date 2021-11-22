package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.bridge.SupplierOrderBridge
import bff.model.Money
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

    @Autowired
    CountryBridge countryBridge

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
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.deliveryCost)
    }

    Money totalMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.total)
    }

    Money subTotalMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.subTotal)
    }

    Money creditsPaidMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.credits_paid)
    }

    Money moneyPaidMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.money_paid)
    }

    Money paymentPendingMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.payment_pending)
    }

    Money totalWabipayMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.total_wabipay)
    }

    Money serviceFeeMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.service_fee)
    }

    Money discountsMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.discounts)
    }

    Money localTaxesMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.localTaxes)
    }

    Money discountUsedMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.discount_used)
    }

    Money amountMoney(SupplierOrderResult supplierOrderRes) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderRes.accessToken)).currency.code, supplierOrderRes.amount)
    }

}
