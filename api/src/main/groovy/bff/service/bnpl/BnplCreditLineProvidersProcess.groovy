package bff.service.bnpl

import bff.model.CreditLineProvider

class BnplCreditLineProvidersProcess {
    private List<Closure<Boolean>> conditions
    private List<CreditLineProvider> successfullyValue
    private List<CreditLineProvider> unsuccessfullyValue

    BnplCreditLineProvidersProcess() {
        this.conditions = []
    }

    BnplCreditLineProvidersProcess nextCondition(Closure<Boolean> condition) {
        conditions.push(condition)
        this
    }

    BnplCreditLineProvidersProcess successfullyValue(List<CreditLineProvider> successfullyValue) {
        this.successfullyValue = successfullyValue
        this
    }

    BnplCreditLineProvidersProcess unsuccessfullyValue(List<CreditLineProvider> unsuccessfullyValue) {
        this.unsuccessfullyValue = unsuccessfullyValue
        this
    }

    List<CreditLineProvider> execute() {
        def result = successfullyValue

        for(def condition : conditions) {
            if(!condition()) {
                result = unsuccessfullyValue
                break
            }
        }

        result
    }
}
