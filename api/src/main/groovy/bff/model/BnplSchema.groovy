package bff.model

import bnpl.sdk.model.CreditLineResponse
import bnpl.sdk.model.PaymentResponse
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.time.Instant

import static bff.model.CreditProvider.SUPERMONEY

//Begin CreditLine related classes ------------------------------------------------------
@ToString
class CreditLinesRequestInput {
    String accessToken
    ScrollInput scrollInput
}

@EqualsAndHashCode
class CreditLines implements CreditLinesResult{
    List<CreditLine> credits
    CreditLinesAction action
    CreditLineProvider provider
    String scroll

    static CreditLines fromSdk(CreditLineResponse creditLineResponse) {
        def currency = creditLineResponse.approvedMoney.currency
        def toRepay = creditLineResponse.approvedMoney.amount - creditLineResponse.unusedMoney.amount
        new CreditLines(
                credits: [
                        new SuperMoneyCreditLine(
                                approvedLimit: new Money(currency: currency, amount: creditLineResponse.approvedMoney.amount),
                                remaining: new Money(currency: currency, amount: creditLineResponse.unusedMoney.amount),
                                toRepay: new Money(currency: currency, amount: toRepay),
                        )
                ],
                action: Optional.ofNullable(creditLineResponse.getRepaymentLink()).map{
                    new ButtonWithUrlCreditLinesAction(
                            redirectUrl: it,
                            provider: SUPERMONEY
                    )
                }.orElse(null),
                provider: new CreditLineProvider(provider: SUPERMONEY)
        )
    }
}

@EqualsAndHashCode
class CreditLineProvider {
    CreditProvider provider
}

enum CreditProvider {
    SUPERMONEY("Supermoney")

    String poweredBy

    CreditProvider(String poweredBy) {
        this.poweredBy = poweredBy
    }
}

interface CreditLinesAction{}

@EqualsAndHashCode
class ButtonWithUrlCreditLinesAction implements CreditLinesAction{
    CreditProvider provider
    URI redirectUrl
}

interface CreditLine {
    Money getApprovedLimit()
}

@EqualsAndHashCode
class SuperMoneyCreditLine implements CreditLine {
    Money approvedLimit
    Money toRepay
    Money remaining
}

interface CreditLinesResult {}
//End CreditLine related classes --------------------------------------------------
//Begin Loan related classes ------------------------------------------------------
@ToString
class LoanPaymentRequestInput {
    Long supplierOrderId
    String accessToken
    Long supplierId
    InvoiceInput invoice
    BigDecimal amount
}

class InvoiceInput {
    String code
    String fileId
}

@EqualsAndHashCode
class LoanPayment implements LoanPaymentResult {
    Long supplierOrderId
    Long customerUserId
    Long supplierId
    Money money
    Loan loan
    Invoice invoice

    static LoanPayment fromSdk(PaymentResponse response) {
        new LoanPayment(
                supplierOrderId: response.supplierOrderId,
                customerUserId: response.customerUserId,
                supplierId: response.supplierId,
                money: new Money(response.money.currency, response.money.amount),
                loan: new Loan(
                        created: fromResponse(response.loan.created),
                        externalId: response.loan.externalId
                ),
                invoice: new Invoice(code: response.invoice.code)
        )
    }

    private static def TimestampOutput fromResponse(Instant date) {
        if (date != null) new TimestampOutput(date.toString()) else null
    }
}

@EqualsAndHashCode
class Loan {
    String externalId
    TimestampOutput created
}

@EqualsAndHashCode
class Invoice {
    String code
}

interface LoanPaymentResult {}

@EqualsAndHashCode
class LoanPaymentFailed implements LoanPaymentResult{
    LoanPaymentFailedReason reason
    String sourceErrorMessage
}

enum LoanPaymentFailedReason {
    INVALID_SUPPLIER_ORDER_ID,
    UNKNOWN

    static LoanPaymentFailedReason findByName(String name){
        Optional.ofNullable(values().find {it.name() == name}).orElse(UNKNOWN)
    }

    def build(String message) {
        new LoanPaymentFailed(reason: this, sourceErrorMessage: message)
    }

}

//End Loan related classes ------------------------------------------------------
