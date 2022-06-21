package bff.model

import bnpl.sdk.model.CreditLineResponse
import bnpl.sdk.model.PaymentResponse
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.time.Instant

import static bff.model.CreditProvider.SUPERMONEY

@ToString
class CreditLinesRequestInput {
    String accessToken
    ScrollInput scrollInput
}

@EqualsAndHashCode
class CreditLines implements CreditLinesResult{
    List<CreditLine> credits
    CreditLinesAction action
    CreditProvider provider
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
                provider: SUPERMONEY
        )
    }
}

@EqualsAndHashCode
class CreditLineProvider {
    CreditProvider provider
}

enum CreditProvider {
    SUPERMONEY
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


@ToString
class LoanPaymentRequestInput {
    Long orderId
    String accessToken
    String supplierId
    String invoiceCode
    BigDecimal amount
}

@EqualsAndHashCode
class LoanPayment implements LoanPaymentResult {
    UUID id
    Long orderId
    String externalId
    String customerId
    String supplierId
    TimestampOutput created
    Money money
    Loan loan
    Invoice invoice

    static def LoanPayment fromSdk(PaymentResponse response) {
        new LoanPayment(
                id: response.id, orderId: response.orderId, customerId: response.customerId, externalId: response.externalId,
                supplierId: response.supplierId, created: fromResponse(response.created),
                money: new Money(response.money.currency, response.money.amount),
                loan: new Loan(id: response.loan.id, created: fromResponse(response.loan.created),
                        approved: fromResponse(response.loan.approved), paid: fromResponse(response.loan.paid),
                        dueDate: fromResponse(response.loan.settlement), status: response.loan.status.name()),
                invoice: new Invoice(id: response.invoice.id, code: response.invoice.code)
        )
    }

    private static def TimestampOutput fromResponse(Instant date) {
        if (date != null) new TimestampOutput(date.toString()) else null
    }
}

@EqualsAndHashCode
class Loan {
    UUID id
    TimestampOutput created
    TimestampOutput approved
    TimestampOutput paid
    TimestampOutput dueDate
    String status
}

@EqualsAndHashCode
class Invoice {
    UUID id
    String code
}

interface LoanPaymentResult {}


