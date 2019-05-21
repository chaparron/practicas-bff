package bff.bridge.http

import bff.bridge.CustomerBridge
import bff.configuration.BadRequestErrorException
import bff.model.AccessTokenInput
import bff.model.Address
import bff.model.Customer
import bff.model.CustomerInput
import bff.model.CustomerStatus
import bff.model.AddressType
import bff.model.CustomerUpdateInput
import bff.model.CustomerUpdateReason
import bff.model.CustomerUpdateResult
import bff.model.DeliveryPreference
import bff.model.ResendVerifyEmailReason
import bff.model.User
import bff.model.UserCredentials
import bff.model.VerificationDocument
import bff.model.VerificationDocumentType
import bff.configuration.ConflictErrorException
import bff.model.VerifyEmailInput
import bff.model.VerifyExpiredFailed
import bff.model.VerifyExpiredReason
import bff.model.VerifyPhoneInput
import groovy.util.logging.Slf4j
import org.apache.http.HttpHeaders
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

    @Override
    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput) {
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.PUT, root.resolve('/customer/me'))
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer $customerUpdateInput.accessToken")
            .body(
                    [
                            phone: customerUpdateInput.phone,
                            username: customerUpdateInput.username,
                            adress: customerUpdateInput.address,
                            deliveryPreference: customerUpdateInput.deliveryPreference,
                            verificationDocuments: customerUpdateInput.verificationDocuments
                    ]
            ), Map).body

            mapCustomer(body)

        } catch(ConflictErrorException conflictErrorException) {
            if(conflictErrorException.innerResponse.error) {
                CustomerUpdateReason.valueOf(conflictErrorException.innerResponse.error).doThrow()
            }
        }
    }

    @Override
    Void verifyEmail(VerifyEmailInput verifyEmailInput) {
        def url = UriComponentsBuilder.fromUri(
                root.resolve("/customer/${verifyEmailInput.id}/verify/email/${verifyEmailInput.token}")).toUriString()
        def uri = url.toURI()
        try {
            def body= http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .build()
                    , Map).body
        } catch (BadRequestErrorException badRequestException) {
            VerifyExpiredReason.TOKEN_EXPIRED.doThrow()
        }
    }

    @Override
    Void resendVerifyEmail(AccessTokenInput accessTokenInput) {

        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/resend/verification/email")).toUriString()
        def uri = url.toURI()

        try {
            def body = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer ${accessTokenInput.accessToken}")
                        .build()
                , Map).body
        } catch(BadRequestErrorException badRequestExcpetion) {
            ResendVerifyEmailReason.NO_VERIFICATION_EMAIL_PENDING.doThow()
        }
    }

    @Override
    Void verifyPhone(VerifyPhoneInput verifyPhoneInput) {
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve("customer/me/verify/phone"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $verifyPhoneInput.accessToken")
                    .body([
                            token: verifyPhoneInput.token
                    ])
            , Map).body
        } catch (BadRequestErrorException badRequestException) {
            VerifyExpiredReason.TOKEN_EXPIRED.doThrow()
        }
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
