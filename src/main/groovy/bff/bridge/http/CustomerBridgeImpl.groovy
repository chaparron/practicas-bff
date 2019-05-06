package bff.bridge.http

import bff.bridge.CustomerBridge
import bff.model.Address
import bff.model.Customer
import bff.model.CustomerInput
import bff.model.CustomerStatus
import bff.model.AddressType
import bff.model.DeliveryPreference
import bff.model.User
import bff.model.UserCredentials
import bff.model.VerificationDocument
import bff.model.VerificationDocumentType
import groovy.util.logging.Slf4j
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import static org.springframework.http.HttpHeaders.AUTHORIZATION


@Slf4j
class CustomerBridgeImpl implements CustomerBridge{
    URI root
    RestOperations http

    @Override
    Customer myProfile(CustomerInput customerInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me")).toUriString()
        def uri = url.toURI()

        def body= http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer ${customerInput.accessToken}")
                        .build()
                , Map).body

        mapCustomer(body)

    }

    static Customer mapCustomer(body) {
        new Customer(
                id: body.id,
                name: body.name,
                enabled: body.enabled,
                legalId: body.legalId,
                linePhone: body.linePhone,
                customerStatus: body.customerStatus as CustomerStatus,
                user: new User(
                        id: body.user.id,
                        username: body.user.username,
                        firstName: body.user.firstName,
                        lastName: body.user.lastName,
                        phone: body.user.phone,
                        credentials: new UserCredentials(
                                id: body.user.credentials?.id,
                                password: body.user.credentials?.password,
                                enabled: body.user.credentials?.enabled,
                        )
                ),
                smsVerification: body.smsVerification,
                emailVerification: body.emailVerification,

                addresses: body.addresses?.collect {
                    new Address(
                            id: it.id,
                            formatted: it.formatted,
                            placeId: it.placeId,
                            addressName: it.addressName,
                            addressNumber: it.addressNumber,
                            city: it.city,
                            postalCode: it.postalCode,
                            state: it.state,
                            lat: it.lat,
                            lon: it.lon,
                            additionalInfo: it.additionalInfo,
                            preferred: it.preferred,
                            addressType: it.addressType as AddressType,
                            enabled: it.enabled
                    )
                },

                verificationDocuments: body.verificationDocuments?.collect {
                    new VerificationDocument(
                            id: it.id,
                            documentType: it.type as VerificationDocumentType
                    )
                },

                deliveryPreference: body.deliveryPreference as DeliveryPreference,
                level: body.level,
                missingDocuments: body.missingDocuments
        )
    }
}
