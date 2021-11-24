package bff.resolver

import bff.model.Money
import bff.model.SupplierOrderAndOrderCancellations
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SupplierOrderAndOrderCancellationsResolver implements GraphQLResolver<SupplierOrderAndOrderCancellations> {

    @Autowired
    MoneyService moneyService

    Money deliveryCostMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.deliveryCost)
    }

    Money totalMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.total)
    }

    Money subTotalMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.subTotal)
    }

    Money creditsPaidMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.credits_paid)
    }

    Money moneyPaidMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.money_paid)
    }

    Money paymentPendingMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.payment_pending)
    }

    Money totalWabipayMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.total_wabipay)
    }

    Money serviceFeeMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.service_fee)
    }

    Money discountsMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.discounts)
    }

    Money localTaxesMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.localTaxes)
    }

    Money discountUsedMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.discount_used)
    }

    Money amountMoney(SupplierOrderAndOrderCancellations supplierOrderAndOrderCancellations) {
        moneyService.getMoney(supplierOrderAndOrderCancellations.accessToken, supplierOrderAndOrderCancellations.amount)
    }
}
