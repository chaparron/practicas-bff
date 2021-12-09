package bff.bridge.sdk.credits

import bff.model.FindSupplierCreditBalancesInput
import groovy.util.logging.Slf4j
import wabi2b.sdk.credits.PageResponse
import wabi2b.sdk.credits.SupplierCreditsSdk

interface CreditService {

    PageResponse findSupplierCreditBalances(FindSupplierCreditBalancesInput input)

}

@Slf4j
class HttpCreditService implements CreditService{

    SupplierCreditsSdk creditsSdk

    PageResponse findSupplierCreditBalances(FindSupplierCreditBalancesInput input){
        try{
            return creditsSdk.findSupplierCreditBalances(input.first, input.after,
                        input.last, input.before, input.accessToken)
        } catch (Exception ex) {
            log.error("Error searching products for input {}", input, ex)
            throw ex
        }
    }

}
