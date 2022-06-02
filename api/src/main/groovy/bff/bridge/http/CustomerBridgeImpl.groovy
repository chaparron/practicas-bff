package bff.bridge.http

import bff.DecoderName
import bff.JwtToken
import bff.bridge.CustomerBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.ConflictErrorException
import bff.model.*
import groovy.util.logging.Slf4j
import io.ktor.client.features.ClientRequestException
import org.apache.commons.lang3.NotImplementedException
import org.apache.http.HttpHeaders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder
import wabi2b.dtos.customers.shared.CustomerDto
import wabi2b.sdk.api.Wabi2bSdk
import wabi2b.sdk.customers.customer.CustomersSdk

import java.time.Duration

import static org.springframework.http.HttpHeaders.AUTHORIZATION

@Slf4j
class CustomerBridgeImpl implements CustomerBridge {
    URI root
    RestOperations http
    @Autowired
    Wabi2bSdk wabi2bSdk
    @Autowired
    CustomersSdk customersSdk
    @Autowired
    CustomerSdkMapper customerSdkMapper


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


        return mapCustomer(body, accessToken)
    }

    @Override
    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput) {
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.PUT, root.resolve('/classiclogin/customer/me'))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $customerUpdateInput.accessToken")
                            .body(
                                    [
                                            phone                : customerUpdateInput.phone,
                                            username             : customerUpdateInput.username,
                                            acceptWhatsApp       : customerUpdateInput.acceptWhatsApp,
                                            adress               : customerUpdateInput.address,
                                            workingDays          : customerUpdateInput.workingDays,
                                            deliveryComment      : customerUpdateInput.deliveryComment,
                                            verificationDocuments: customerUpdateInput.verificationDocuments,
                                            marketingEnabled     : customerUpdateInput.marketingEnabled
                                    ]
                            ), Customer).body
            return mapCustomer(body, customerUpdateInput.accessToken)
        } catch (ConflictErrorException conflictErrorException) {
            mapCustomerError(conflictErrorException, "Update Customer Profile Error")
        }
    }

    @Override
    CustomerUpdateResult updateProfileV2(CustomerUpdateInputV2 customerUpdateInput) {
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.PUT, root.resolve('/customer/me'))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $customerUpdateInput.accessToken")
                            .body(
                                    [
                                            username             : customerUpdateInput.username,
                                            acceptWhatsApp       : customerUpdateInput.acceptWhatsApp,
                                            adress               : customerUpdateInput.address,
                                            workingDays          : customerUpdateInput.workingDays,
                                            deliveryComment      : customerUpdateInput.deliveryComment,
                                            verificationDocuments: customerUpdateInput.verificationDocuments,
                                            marketingEnabled     : customerUpdateInput.marketingEnabled
                                    ]
                            ), Customer).body
            return mapCustomer(body, customerUpdateInput.accessToken)

        } catch (ConflictErrorException conflictErrorException) {
            mapCustomerError(conflictErrorException, "Update Customer Profile Error")
        }
    }

    @Override
    CredentialsCustomerResponse signIn(SignInInput signInInput) {
        def body = http.exchange(
                RequestEntity.method(HttpMethod.POST, root.resolve('/classiclogin/customer'))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(signInInput)
                , CredentialsCustomerResponse).body
        return body
    }

    @Override
    Customer passwordlessSignUp(PasswordlessSignUpInput passwordlessSignUpInput, String remoteAddress) {
        def body = http.exchange(
                RequestEntity.method(HttpMethod.POST, root.resolve('/customer'))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Address-Received-In-Bff", remoteAddress)
                        .header("Recaptcha-Token", passwordlessSignUpInput.captchaToken)
                        .body(passwordlessSignUpInput)
                , Customer).body


        return mapCustomer(body, null)

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
                                        addressType   : addressInput.addressType,
                                        postalCode    : addressInput.postalCode
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
                                            addressType   : addressInput.addressType,
                                            postalCode    : addressInput.postalCode,
                                            state         : addressInput.state
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
        catch (Exception ex) {
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

    @Override
    PreSignedObject findCustomerLegalDocument(FindCustomerLegalDocumentInput findCustomerLegalDocumentInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/legal/document/${findCustomerLegalDocumentInput.documentId}"))
                .toUriString().toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(AUTHORIZATION, "Bearer $findCustomerLegalDocumentInput.accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , PreSignedObject).body

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

    List<SuppliersNameResult> getSuppliersThatHasSuggestedOrders(String accessToken) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplier/suggestedOrder")).toUriString()
        def uri = url.toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , new ParameterizedTypeReference<List<SuppliersNameResult>>() {}).body
    }

    SuggestedOrderResult getSuggestedOrder(GetSuggestedOrderInput input) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplier/suggestedOrder/${input.supplierId}")).toUriString()
        def uri = url.toURI()
        SuggestedOrderResult result = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $input.accessToken")
                        .build()
                , new ParameterizedTypeReference<SuggestedOrderResult>() {}).body
        if (result) {
            result.accessToken = input.accessToken
        }
        return result
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
    List<SupplierOrder> findPendingRateSinceLastLogin(String accessToken) {
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

    @Override
    BranchOfficesResponse getMyBranchOffices(String accessToken, Long page, Long size) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/branch-office"))
                .queryParam("page", page)
                .queryParam("size", size)
                .toUriString()
        def uri = url.toURI()

        def response = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , new ParameterizedTypeReference<BranchOfficesResponse>() {}).body

        response.accessToken = accessToken
        response.content.each {
            it.accessToken = accessToken
            it.user.accessToken = accessToken
        }
        response
    }

    @Override
    Customer getBranchOffice(String accessToken, String branchOfficeId) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/customer/me/branch-office/${branchOfficeId}")).toUriString()
        def uri = url.toURI()

        def body = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer ${accessToken}")
                        .build()
                , Customer).body


        return mapCustomer(body, accessToken)
    }

    @Override
    Void enableBranchOffice(String accessToken, String branchOfficeId) {
        wabi2bSdk.enableBranchOffice(branchOfficeId, accessToken).block(Duration.ofMillis(30000))
        return Void.SUCCESS
    }

    @Override
    Void disableBranchOffice(String accessToken, String branchOfficeId) {
        wabi2bSdk.disableBranchOffice(branchOfficeId, accessToken).block(Duration.ofMillis(30000))
        return Void.SUCCESS
    }

    @Override
    Long countTotalBranchOffice(String accessToken) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/branch-office/count")).toUriString().toURI()

        def response = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , new ParameterizedTypeReference<Map<String, Long>>() {}).body

        response.get("total")
    }

    @Override
    Long countActiveBranchOffice(String accessToken) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/branch-office/count")).toUriString().toURI()

        def response = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , new ParameterizedTypeReference<Map<String, Long>>() {}).body

        response.get("active")
    }

    @Override
    AddBranchOfficeResult addBranchOffice(AddBranchOfficeInput addBranchOfficeInput) {
        CustomerDto customerDto
        try {
            customerDto = customersSdk.createBranchOffice(
                    customerSdkMapper.toDto(addBranchOfficeInput),
                    addBranchOfficeInput.accessToken
            )
            Customer customer = customerSdkMapper.toCustomer(customerDto)
            customer = mapCustomer(customer, addBranchOfficeInput.accessToken)
            customer.marketingEnabledForcedInResponse = addBranchOfficeInput.marketingEnabled
            return customer
        } catch (ClientRequestException ex) {
            def reason = AddBranchOfficeFailedReason.valueFor(ex.message)
            if (reason != null) {
                return reason.build()
            }
            throw ex
        }
    }

    @Override
    Customer updateBranchOfficeProfile(UpdateBranchOfficeProfileInput input) {
        CustomerDto customerDto = customersSdk.mainOfficeUpdateBranchOfficeProfile(
                input.branchOfficeId,
                customerSdkMapper.toDto(input),
                input.accessToken
        )
        Customer customer = customerSdkMapper.toCustomer(customerDto)
        customer = mapCustomer(customer, input.accessToken)
        customer.marketingEnabledForcedInResponse = input.marketingEnabled
        return customer
    }

    @Override
    User getUserById(String accessToken, Long userId) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/user/${userId}")).toUriString()
        def uri = url.toURI()

        def user = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, prepareAccessToken(accessToken))
                        .build()
                , User).body
        user.accessToken = accessToken
        return user
    }

    private static String prepareAccessToken(String token) {
        if (token.startsWith("Bearer ")) {
            return token
        }
        return "Bearer ${token}"
    }

    private Customer mapCustomer(Customer customer, String accessToken) {
        customer.customerType.id = customer.customerType.code
        customer.accessToken = accessToken
        customer.user?.accessToken = accessToken
        customer
    }




}
