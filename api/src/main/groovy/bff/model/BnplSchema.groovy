package bff.model

import bnpl.sdk.model.CreditLineResponse
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

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

    static def CreditLines fromSdk(CreditLineResponse creditLineResponse) {
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
                action: new ButtonWithUrlCreditLinesAction(
                        redirectUrl: URI.create("http://unaUrlDePruebaFake.com"),
                        provider: SUPERMONEY
                ),
                provider: SUPERMONEY
        )
    }
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
