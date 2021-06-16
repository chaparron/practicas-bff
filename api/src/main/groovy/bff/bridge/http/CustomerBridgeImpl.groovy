package bff.bridge.http

import bff.DecoderName
import bff.JwtToken
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

import static org.springframework.http.HttpHeaders.AUTHORIZATION

@Slf4j
class CustomerBridgeImpl implements CustomerBridge {
    URI root
    RestOperations http

    @Override
    Customer myProfile(String accessToken) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me")).toUriString()
        def uri = url.toURI()

        def body = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer ${accessToken}")
                        .build()
                , Customer).body

        body.accessToken = accessToken
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
                                            phone                      : customerUpdateInput.phone,
                                            username                   : customerUpdateInput.username,
                                            acceptWhatsApp             : customerUpdateInput.acceptWhatsApp,
                                            adress                     : customerUpdateInput.address,
                                            workingDays                : customerUpdateInput.workingDays,
                                            deliveryComment            : customerUpdateInput.deliveryComment,
                                            verificationDocuments      : customerUpdateInput.verificationDocuments,
                                            marketingEnabled           : customerUpdateInput.marketingEnabled
                                    ]
                            ), Customer).body
            body.accessToken = customerUpdateInput.accessToken
            return body

        } catch (ConflictErrorException conflictErrorException) {
            mapCustomerError(conflictErrorException, "Update Customer Profile Error")
        }
    }

    @Override
    CredentialsCustomerResponse signIn(SignInInput signInInput) {
        def body = http.exchange(
                RequestEntity.method(HttpMethod.POST, root.resolve('/customer'))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(signInInput)
                , CredentialsCustomerResponse).body
        return body
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
            Void.SUCCESS
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
            return Void.SUCCESS
        } catch (BadRequestErrorException badRequestExcpetion) {
            mapCustomerError(badRequestExcpetion, "Resend Verify Customer Email Error")
        }
    }

    @Override
    Void verifyPhone(VerifyPhoneInput verifyPhoneInput) {
        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve("/customer/me/verify/phone"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $verifyPhoneInput.accessToken")
                            .body([
                                    token: verifyPhoneInput.token
                            ])
                    , Map).body
            return Void.SUCCESS
        } catch (BadRequestErrorException badRequestException) {
            mapCustomerError(badRequestException, "Verify Customer Phone Error")
        }
    }

    @Override
    Void resendVerifySMS(AccessTokenInput accessTokenInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/resend/verification/sms")).toUriString()
        def uri = url.toURI()

        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer ${accessTokenInput.accessToken}")
                            .build()
                    , Map).body
            return Void.SUCCESS
        } catch (BadRequestErrorException badRequestException) {
            mapCustomerError(badRequestException, "Resend Verify Customer SMS")
        }
    }

    @Override
    List<Address> findAddresses(AccessTokenInput accessTokenInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/address")).toUriString()
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
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/addresses")).toUriString()
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
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/verificationDocs")).toUriString()
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
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/address/preferred/${preferredAddressInput.addressId}")).toUriString()
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
    Address getAddress(AddressIdInput addressIdInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/address/${addressIdInput.address_id}"))
                .toUriString().toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(AUTHORIZATION, "Bearer $addressIdInput.accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , Address).body
    }

    Address getPreferredAddress(String accessToken) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/address/preferred"))
                .toUriString().toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , Address).body
    }

    @Override
    List<CustomerCancelOptionReason> getCancelOptions(String accessToken) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order/cancelOrder-options")).toUriString()
        def uri = url.toURI()

        try {
            def ref = new ParameterizedTypeReference<List<CustomerCancelOptionReason>>() {}
            http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer ${accessToken}")
                            .build()
                    , ref).body

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Find Cancel Options  - Backend Error", badRequestException)
        }
    }

    @Override
    Void addAddress(AddressInput addressInput) throws BadRequestErrorException {
        http.exchange(
                RequestEntity.method(HttpMethod.POST, root.resolve("/customer/me/address"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $addressInput.accessToken")
                        .body(
                                [
                                        id            : addressInput.id,
                                        formatted     : addressInput.formatted,
                                        state         : addressInput.state,
                                        lat           : addressInput.lat,
                                        lon           : addressInput.lon,
                                        additionalInfo: addressInput.additionalInfo,
                                        addressType   : addressInput.addressType
                                ]
                        ), Map).body


        def id = JwtToken.fromString(addressInput.accessToken, DecoderName.ENTITY_ID).name
        return new Void(voidReason: VoidReason.SUCCESS, id: Integer.parseInt(id), entityType: EntityType.CUSTOMER)

    }

    @Override
    Void updateAddress(AddressInput addressInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/address/${addressInput.id}")).toUriString()
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
                                            lat           : addressInput.lat,
                                            lon           : addressInput.lon,
                                            additionalInfo: addressInput.additionalInfo,
                                            addressType   : addressInput.addressType
                                    ]
                            ), Map).body


            def id = JwtToken.fromString(addressInput.accessToken, DecoderName.ENTITY_ID).name
            return new Void(voidReason: VoidReason.SUCCESS, id: Integer.parseInt(id), entityType: EntityType.CUSTOMER)

        } catch (BadRequestErrorException badRequestErrorException) {
            mapCustomerError(badRequestErrorException, "Update Address Error")
        }
    }

    @Override
    Void deleteAddress(AddressIdInput addressIdInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/address/${addressIdInput.address_id}")).toUriString()
        def uri = url.toURI()
        http.exchange(
                RequestEntity.method(HttpMethod.DELETE, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $addressIdInput.accessToken")
                        .build()
                , Map).body
        Void.SUCCESS

    }

    @Override
    Boolean customerHasOrders(AccessTokenInput accessTokenInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order/exist")).toUriString()
        def uri = url.toURI()
        try {
            return http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer $accessTokenInput.accessToken")
                            .build()
                    , Boolean).body
        }
        catch(Exception ex) {
            //Hide this exception temporally.
        }
        return false
    }

    @Override
    Integer getPendingRatesCount(AccessTokenInput accessTokenInput) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/rating/pending/count")).toUriString()
        def uri = url.toURI()
        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessTokenInput.accessToken")
                        .build()
                , Integer).body
    }

    @Override
    SupplierRatingsResponse getSupplierRatings(String accessToken, Long supplierId, Long page, Long size) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/rating/supplier/${supplierId}"))
                .queryParam("page", page)
                .queryParam("size", size)
                .toUriString()
        def uri = url.toURI()
        def response = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , SupplierRatingsResponse).body

        response.content.each {
            it.accessToken = accessToken
            it.customerName = it.customer.name
            it
        }
        response
    }

    @Override
    List<SupplierOrder> getSupplierOrdersPendingToRate(String accessToken) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/rating/pending")).toUriString()
        def uri = url.toURI()

        def supplierOrders = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , new ParameterizedTypeReference<List<SupplierOrder>>() {}).body

        supplierOrders.collect {
            it.accessToken = accessToken
            it
        }
    }

    @Override
    CustomerRateSupplierResult customerRateSupplier(String accessToken, Integer supplierOrderId, Integer supplierId, String opinion, Integer score) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/rating/rate/supplier")).toUriString()
        def uri = url.toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .body([
                                accessToken    : accessToken,
                                supplierOrderId: supplierOrderId,
                                supplierId     : supplierId,
                                opinion        : opinion,
                                score          : score
                        ])
                , Map).body

        Void.SUCCESS
    }

    @Override
    CustomerReportRateResult customerReportRate(String accessToken, Integer rateId) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/rating/report/${rateId}")).toUriString()
        def uri = url.toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.PUT, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , Map).body

        Void.SUCCESS
    }

    static def mapCustomerError(RuntimeException exception, String error) {
        if (exception.innerResponse) {
            CustomerErrorReason.valueOf((String) exception.innerResponse).doThrow()
        } else {
            throw new NotImplementedException(error, exception)
        }
    }

    @Override
    Void enableWhatsApp(AccessTokenInput input) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/whatsapp/enable")).toUriString()
        def uri = url.toURI()
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.PUT, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer $input.accessToken")
                            .build()
                    , Map).body
        } catch (BadRequestErrorException badRequestException) {
            mapCustomerError(badRequestException, "Enable whatsapp Error ")
        }
        Void.SUCCESS
    }

    @Override
    Void disableWhatsApp(AccessTokenInput input) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/whatsapp/disable")).toUriString()
        def uri = url.toURI()
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.PUT, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer $input.accessToken")
                            .build()
                    , Map).body
        } catch (BadRequestErrorException badRequestException) {
            mapCustomerError(badRequestException, "Disable whatsapp Error ")
        }
        Void.SUCCESS
    }

    @Override
    Void userDevice(UserDeviceInput input) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/userDevice")).toUriString()
        def uri = url.toURI()
        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.POST, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer $input.accessToken")
                            .body([
                                    accessToken: input.accessToken,
                                    os         : input.os,
                                    pushToken  : input.pushToken,
                                    appVersion : input.appVersion
                            ])
                    , Map).body
        } catch (BadRequestErrorException ignored) {
        }
        Void.SUCCESS
    }

    @Override
    Void deleteUserDevice(AccessTokenInput input) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/userDevice")).toUriString()
        def uri = url.toURI()
        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.DELETE, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer $input.accessToken")
                            .body([
                                    accessToken: input.accessToken,
                            ])
                    , Map).body
        } catch (BadRequestErrorException ignored) {
        }
        Void.SUCCESS
    }

    List<SuppliersNameResult> getSuppliersThatHasSuggestedOrders(String accessToken){
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplier/suggestedOrder")).toUriString()
        def uri = url.toURI()

       http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , new ParameterizedTypeReference<List<SuppliersNameResult>>() {}).body
    }

    SuggestedOrderResult getSuggestedOrder(GetSuggestedOrderInput input){
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplier/suggestedOrder/${input.supplierId}")).toUriString()
        def uri = url.toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $input.accessToken")
                        .build()
                , new ParameterizedTypeReference<SuggestedOrderResult>() {}).body
    }

    @Override
    Void markSuggestionAsRead(String accessToken, List<Long> supplierIds) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplier/suggestedOrder")).toUriString()
        def uri = url.toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .body(supplierIds)
                , Map).body
        Void.SUCCESS
    }

    @Override
    Void acceptTc(AcceptTcInput input) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/tc")).toUriString()
        def uri = url.toURI()
        http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer ${input.accessToken}")
                        .body(input)
                , Map).body
        Void.SUCCESS
    }

    @Override
    List<SupplierOrder> findPendingRateSinceLastLogin(String accessToken){
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/rating/pending/latest")).toUriString()
        def uri = url.toURI()

        def latestPendingRates = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , new ParameterizedTypeReference<List<SupplierOrder>>() {}).body

        latestPendingRates.collect {
            it.accessToken = accessToken
            it
        }
    }

}
