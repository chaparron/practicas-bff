package bff.service.bnpl

import bff.model.CreditLineProvider
import groovy.util.logging.Slf4j

@Slf4j
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

        int index = 1
        for (def condition : conditions) {
            if (!condition()) {
                log.trace("Unsatisfied condition $index")
                result = unsuccessfullyValue
                break
            }
            index++
        }

        result
    }
}
