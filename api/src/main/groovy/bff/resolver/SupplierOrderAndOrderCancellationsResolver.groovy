package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.model.Money
import bff.model.SupplierOrderAndOrderCancellations
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierOrderAndOrderCancellationsResolver implements GraphQLResolver<SupplierOrderAndOrderCancellations> {

    @Autowired
    CountryBridge countryBridge

    Money deliveryCostMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.deliveryCost)
    }

    Money totalMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.total)
    }

    Money subTotalMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.subTotal)
    }

    Money creditsPaidMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.credits_paid)
    }

    Money moneyPaidMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.money_paid)
    }

    Money paymentPendingMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.payment_pending)
    }

    Money totalWabipayMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.total_wabipay)
    }

    Money serviceFeeMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.service_fee)
    }

    Money discountsMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.discounts)
    }

    Money localTaxesMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.localTaxes)
    }

    Money discountUsedMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.discount_used)
    }

    Money amountMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(supplierOrderAndOrderCancellations.accessToken)).currency.code, supplierOrderAndOrderCancellations.amount)
    }
}
