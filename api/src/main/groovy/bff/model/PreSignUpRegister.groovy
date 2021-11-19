package bff.model

import bff.bridge.DataRegisterBridge
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PreSignUpRegister implements GraphQLMutationResolver {

    @Autowired
    DataRegisterBridge dataRegisterBridge

    @Value('${google.spreadsheet.id.presignup:}')
    String googleSpreadsheetIdPreSignUp

    Boolean register(PreSignUpInput input) {
        dataRegisterBridge.sendMessage(
                googleSpreadsheetIdPreSignUp,
                [
                        input.name,
                        input.lastName,
                        input.countryCode,
                        input.phone,
                        input.alternativePhone ?: "",
                        input.email ?: "",
                        input.country ?: ""
                ]
        )
    }
}
