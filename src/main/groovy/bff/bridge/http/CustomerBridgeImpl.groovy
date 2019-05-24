package bff.bridge.http

import bff.bridge.CustomerBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.ConflictErrorException
import bff.model.AddressInput
import bff.model.CustomerUpdateResult
import bff.model.CustomerInput
import bff.model.CustomerUpdateInput
import bff.model.PreferredAddressInput
import bff.model.VerifyEmailInput
import bff.model.VerifyPhoneInput
import bff.model.AccessTokenInput
import bff.model.Address
import bff.model.Customer
import bff.model.User
import bff.model.UserCredentials
import bff.model.VerificationDocument
import bff.model.CustomerErrorReason
import bff.model.CustomerStatus
import bff.model.AddressMode
import bff.model.DeliveryPreference
import bff.model.VerificationDocumentType

import groovy.util.logging.Slf4j

import org.apache.http.HttpHeaders
import org.apache.commons.lang3.NotImplementedException
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
            mapCustomerError(conflictErrorException, "Update Customer Profile Error")
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
            mapCustomerError(badRequestException, "Verify Customer Email Error")
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
            mapCustomerError(badRequestExcpetion, "Resend Verify Customer Email Error")
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
            mapCustomerError(badRequestException, "Verify Customer Phone Error")
        }
    }

    @Override
    Void resendVerifySMS(AccessTokenInput accessTokenInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("customer/me/resend/verification/sms")).toUriString()
        def uri = url.toURI()

        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer ${accessTokenInput.accessToken}")
                    .build()
            , Map).body

        } catch (BadRequestErrorException badRequestException) {
            mapCustomerError(badRequestException, "Resend Verify Customer SMS")
        }
    }

    @Override
    Void setPreferredAddress(PreferredAddressInput preferredAddressInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("customer/me/address/preferred/${preferredAddressInput.addressId}")).toUriString()
        def uri = url.toURI()
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.PUT, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer $preferredAddressInput.accessToken")
                    .build()
            , Map).body
        } catch (BadRequestErrorException badRequestException) {
            mapCustomerError(badRequestException, "Set Preferred Customer Address Error ")
        }
    }

    @Override
    Void addAddress(AddressInput addressInput) {
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve("customer/me/address"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $addressInput.accessToken")
                            .body(
                            [
                                    id : addressInput.id,
                                    formatted: addressInput.formatted,
                                    placeId: addressInput.placeId,
                                    addresName: addressInput.addressName,
                                    addressNumber: addressInput.addressNumber,
                                    city: addressInput.city,
                                    postalCode: addressInput.postalCode,
                                    state: addressInput.state,
                                    lat : addressInput.lat,
                                    lon: addressInput.lon,
                                    additionalInfo: addressInput.additionalInfo,
                                    preferred: addressInput.preferred,
                                    enabled: addressInput.enabled
                            ]
                    ), Map).body

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Add Address - Backend Error" , badRequestException)
        }
    }

    static def mapCustomerError(RuntimeException exception, String error) {
        if (exception.innerResponse) {
            CustomerErrorReason.valueOf((String) exception.innerResponse).doThrow()
        } else {
            throw new NotImplementedException(error, exception)
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
                            addressType: it.addressType as AddressMode,
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
