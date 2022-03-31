package bff.resolver

import bff.bridge.CustomerBridge
import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BranchOfficeResolver implements GraphQLResolver<BranchOfficesResponse> {

    @Autowired
    CustomerBridge customerBridge

    Long total(BranchOfficesResponse branchOfficesResponse) {
        branchOfficesResponse.total = customerBridge.countTotalBranchOffice(branchOfficesResponse.accessToken)
    }

    Long active(BranchOfficesResponse branchOfficesResponse) {
        branchOfficesResponse.active = customerBridge.countActiveBranchOffice(branchOfficesResponse.accessToken)
    }

}
