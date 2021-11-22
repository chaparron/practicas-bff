package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.bridge.SupplierOrderBridge
import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierOrderResolver implements GraphQLResolver<SupplierOrder> {

    @Autowired
    SupplierOrderBridge supplierOrderBridge

    @Autowired
    CountryBridge countryBridge

    Supplier supplier(SupplierOrder supplierOrder) {
        supplierOrderBridge.getSupplierBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    List<OrderItem> products(SupplierOrder supplierOrder) {
        supplierOrderBridge.getOrderItemsBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    RatingEntry rating(SupplierOrder supplierOrder) {
        if (!supplierOrder.rating && supplierOrder.ratings && supplierOrder.ratings.size() > 0) {
            def supplierRating = supplierOrder.ratings.get(RatingOwner.SUPPLIER)
            supplierRating?.accessToken = supplierOrder.accessToken

            def customerRating = supplierOrder.ratings.get(RatingOwner.CUSTOMER)
            customerRating?.accessToken = supplierOrder.accessToken

            return new RatingEntry(
                    SUPPLIER: supplierRating,
                    CUSTOMER: customerRating
            )
        }
        supplierOrder.rating?:supplierOrderBridge.getRatingBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    Order order(SupplierOrder supplierOrder) {
        supplierOrderBridge.getOrderBySupplierOrderId(supplierOrder.accessToken, supplierOrder.id)
    }

    Money deliveryCostMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.deliveryCost)
    }

    Money totalMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.total)
    }

    Money subTotalMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.subTotal)
    }

    Money creditsPaidMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.credits_paid)
    }

    Money moneyPaidMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.money_paid)
    }

    Money paymentPendingMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.payment_pending)
    }

    Money totalWabipayMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.total_wabipay)
    }

    Money serviceFeeMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.service_fee)
    }

    Money discountsMoney(SupplierOrder supplierOrder) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrder.accessToken)).currency.code, supplierOrder.discounts)
    }

}
