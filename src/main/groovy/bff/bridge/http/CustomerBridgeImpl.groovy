package bff.bridge.http

import bff.bridge.CustomerBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.ConflictErrorException
import bff.model.*
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.NotImplementedException
import org.apache.http.HttpHeaders
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import java.lang.Void

import static org.springframework.http.HttpHeaders.AUTHORIZATION

@Slf4j
class CustomerBridgeImpl implements CustomerBridge {
    URI root
    RestOperations http

    @Override
    Customer myProfile(CustomerInput customerInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me")).toUriString()
        def uri = url.toURI()

        def body = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer ${customerInput.accessToken}")
                .build()
            , Customer).body

        body.accessToken = customerInput.accessToken
        return body
    }

    @Override
    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput) {
        try {
            def body = http.exchange(
                RequestEntity.method(HttpMethod.PUT, root.resolve('/customer/me'))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $customerUpdateInput.accessToken")
                    .body(
                    [
                        phone                : customerUpdateInput.phone,
                        username             : customerUpdateInput.username,
                        adress               : customerUpdateInput.address,
                        deliveryPreference   : customerUpdateInput.deliveryPreference,
                        verificationDocuments: customerUpdateInput.verificationDocuments
                    ]
                ), Customer).body
            body.accessToken = customerUpdateInput.accessToken
            return body

        } catch (ConflictErrorException conflictErrorException) {
            mapCustomerError(conflictErrorException, "Update Customer Profile Error")
        }
    }

    @Override
    Void verifyEmail(VerifyEmailInput verifyEmailInput) {
        def url = UriComponentsBuilder.fromUri(
            root.resolve("/customer/${verifyEmailInput.id}/verify/email/${verifyEmailInput.token}")).toUriString()
        def uri = url.toURI()
        try {
            def body = http.exchange(
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
            http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer ${accessTokenInput.accessToken}")
                    .build()
                , Map).body
            return
        } catch (BadRequestErrorException badRequestExcpetion) {
            mapCustomerError(badRequestExcpetion, "Resend Verify Customer Email Error")
        }
    }

    @Override
    Void verifyPhone(VerifyPhoneInput verifyPhoneInput) {
        try {
            http.exchange(
                RequestEntity.method(HttpMethod.POST, root.resolve("customer/me/verify/phone"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $verifyPhoneInput.accessToken")
                    .body([
                    token: verifyPhoneInput.token
                ])
                , Map).body
            return
        } catch (BadRequestErrorException badRequestException) {
            mapCustomerError(badRequestException, "Verify Customer Phone Error")
        }
    }

    @Override
    Void resendVerifySMS(AccessTokenInput accessTokenInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("customer/me/resend/verification/sms")).toUriString()
        def uri = url.toURI()

        try {
            http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer ${accessTokenInput.accessToken}")
                    .build()
                , Map).body
            return

        } catch (BadRequestErrorException badRequestException) {
            mapCustomerError(badRequestException, "Resend Verify Customer SMS")
        }
    }

    @Override
    List<Address> findAddresses(AccessTokenInput accessTokenInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("customer/me/address")).toUriString()
        def uri = url.toURI()

        try {
            def ref = new ParameterizedTypeReference<List<Address>>() {}
            http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer ${accessTokenInput.accessToken}")
                    .build()
                , ref).body

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Find Customer Addresses  - Backend Error", badRequestException)
        }
    }

    @Override
    List<Address> findAddressesByCustomerAccessToken(String accessToken) {
        def url = UriComponentsBuilder.fromUri(root.resolve("customer/me/addresses")).toUriString()
        def uri = url.toURI()

        try {
            def ref = new ParameterizedTypeReference<List<Address>>() {}
            http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer ${accessToken}")
                    .build()
                , ref).body

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Find Customer Addresses  - Backend Error", badRequestException)
        }
    }

    @Override
    List<VerificationDocument> findVerificationDocs(String accessToken) {
        def url = UriComponentsBuilder.fromUri(root.resolve("customer/me/verificationDocs")).toUriString()
        def uri = url.toURI()

        try {
            def ref = new ParameterizedTypeReference<List<VerificationDocument>>() {}
            http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer ${accessToken}")
                    .build()
                , ref).body

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Find Customer Addresses  - Backend Error", badRequestException)
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
            http.exchange(
                RequestEntity.method(HttpMethod.POST, root.resolve("customer/me/address"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $addressInput.accessToken")
                    .body(
                    [
                        id            : addressInput.id,
                        formatted     : addressInput.formatted,
                        placeId       : addressInput.placeId,
                        addresName    : addressInput.addressName,
                        addressNumber : addressInput.addressNumber,
                        city          : addressInput.city,
                        postalCode    : addressInput.postalCode,
                        state         : addressInput.state,
                        lat           : addressInput.lat,
                        lon           : addressInput.lon,
                        additionalInfo: addressInput.additionalInfo,
                        preferred     : addressInput.preferred,
                        addressType   : addressInput.addressType,
                        enabled       : addressInput.enabled
                    ]
                ), Map).body
            return
        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Add Customer Address - Backend Error", badRequestException)
        }
    }

    @Override
    Void updateAddress(AddressInput addressInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("customer/me/address/${addressInput.id}")).toUriString()
        def uri = url.toURI()
        try {
            http.exchange(
                RequestEntity.method(HttpMethod.PUT, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer $addressInput.accessToken")
                    .body(
                    [
                        id            : addressInput.id,
                        formatted     : addressInput.formatted,
                        placeId       : addressInput.placeId,
                        addresName    : addressInput.addressName,
                        addressNumber : addressInput.addressNumber,
                        city          : addressInput.city,
                        postalCode    : addressInput.postalCode,
                        state         : addressInput.state,
                        lat           : addressInput.lat,
                        lon           : addressInput.lon,
                        additionalInfo: addressInput.additionalInfo,
                        preferred     : addressInput.preferred,
                        addressType   : addressInput.addressType,
                        enabled       : addressInput.enabled
                    ]
                ), Map).body
            return
        } catch (BadRequestErrorException badRequestErrorException) {
            mapCustomerError(badRequestErrorException, "Update Address Error")
        }
    }

    @Override
    Void deleteAddress(AddressIdInput addressIdInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("customer/me/address/${addressIdInput.address_id}")).toUriString()
        def uri = url.toURI()
        try {
            http.exchange(
                RequestEntity.method(HttpMethod.DELETE, uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer $addressIdInput.accessToken")
                    .build()
                , Map).body
            return
        } catch (BadRequestErrorException badRequestErrorException) {
            mapCustomerError(badRequestErrorException, "Update Address Error")
        }
    }

    static def mapCustomerError(RuntimeException exception, String error) {
        if (exception.innerResponse) {
            CustomerErrorReason.valueOf((String) exception.innerResponse).doThrow()
        } else {
            throw new NotImplementedException(error, exception)
        }
    }

}
