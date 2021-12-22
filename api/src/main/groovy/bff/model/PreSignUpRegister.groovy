package bff.model

import bff.bridge.DataRegisterBridge
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class PreSignUpRegister implements GraphQLMutationResolver {

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))

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
                        input.country ?: "",
                        DATE_TIME_FORMATTER.format(Instant.now())
                ]
        )
    }
}
