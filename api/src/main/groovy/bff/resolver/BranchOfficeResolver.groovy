package bff.resolver

import bff.bridge.CustomerBridge
import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BranchOfficeResolver implements GraphQLResolver<BranchOffice> {

    @Autowired
    CustomerBridge customerBridge

    Long total(BranchOffice branchOffice) {
        branchOffice.total(customerBridge.countTotalBranchOffice(branchOffice.content.accessToken))
    }

    Long active(BranchOffice branchOffice) {
        branchOffice.active(customerBridge.countActiveBranchOffice(branchOffice.content.accessToken))
    }
}
