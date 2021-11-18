package bff.model

import bff.bridge.DataRegisterBridge
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SupplierLead implements GraphQLMutationResolver {

    @Autowired
    DataRegisterBridge dataRegisterBridge

    @Value('${google.spreadsheet.id.supplier.lead:}')
    String googleSpreadsheetIdSupplierLead

    Boolean supplierLead(SupplierLeadInput input) {
        dataRegisterBridge.sendMessage(
                googleSpreadsheetIdSupplierLead,
                [input.countryId, input.businessName, input.haveDistribution?.toString() ?: "", input.city, input.contactName,
                 input.contactPhoneNumber, input.contactEmail ?: "", input.howMeetUs?.type ?: "", input.howMeetUs?.detail ?: ""]
        )
    }
}
