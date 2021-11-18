package bff.model

import bff.DecoderName
import bff.JwtToken
import bff.bridge.*
import bff.configuration.BadRequestErrorException
import bff.configuration.ConflictErrorException
import bff.configuration.EntityNotFoundException
import bff.configuration.NotAcceptableException
import bff.model.utils.DataURL
import bff.service.DeviceIdentifierService
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import graphql.schema.DataFetchingEnvironment
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Slf4j
@Component
class Mutation implements GraphQLMutationResolver {

    @Autowired
    AuthServerBridge authServerBridge

    @Autowired
    CustomerBridge customerBridge

    @Autowired
    OrderBridge orderBridge

    @Autowired
    DocumentBridge documentBridge

    @Autowired
    RecommendedOrderBridge recommendedOrderBridge

    @Value('${environment:}')
    String environment

    // TODO: remove
    private final List<String> legacyCountries = []

    LoginResult login(LoginInput input) {
        try {
            def credentials = passwordLogin(input.username, input.password, input.site)
            return resolveCredentialsResponse(credentials, input.supportLegacy)

        } catch (LoginFailureException loginException) {
            loginException.build()
        }
    }

    SignedChallengeDemandResult challengeRequestForChangeToPasswordlessAuthentication(SignedChallengeDemandInput input, DataFetchingEnvironment env) {

        def remoteAddress = DeviceIdentifierService.identifySource(env)

        try {
            authServerBridge.challengeRequestForChangeToPasswordlessAuthentication(input.countryCode, input.phone, input.accessToken, remoteAddress)
        } catch (TooManyShipmentsException tooManyShipmentsException) {
            tooManyShipmentsException.build()
        } catch (SignedChallengeDemandFailureException signedChallengeDemandFailureException) {
            signedChallengeDemandFailureException.build()
        }
    }

    SignedChallengeAnswerResult challengeAnswerForChangeToPasswordlessAuthentication(SignedChallengeAnswer input) {
        try {
            def credentials = authServerBridge.challengeAnswerForChangeToPasswordlessAuthentication(input.challengeId, input.challengeAnswer, input.accessToken)
            new GenericCredentials(
                    username: JwtToken.fromString(credentials.accessToken, DecoderName.USERNAME).name,
                    credentials: credentials,
                    customer: customerBridge.myProfile(credentials.accessToken)
            )
        } catch (ChallengeAnswerFailureException challengeAnswerFailureException) {
            challengeAnswerFailureException.build()
        }
    }

    ChallengeDemandResult challengeRequestForPasswordlessLogin(ChallengeDemandInput input, DataFetchingEnvironment env) {
        def remoteAddress = DeviceIdentifierService.identifySource(env)
        try {
            authServerBridge.challengeRequestForPasswordlessLogin(input.countryCode, input.phone, remoteAddress)
        } catch (TooManyShipmentsException tooManyShipmentsException) {
            tooManyShipmentsException.build()
        } catch (ChallengeDemandFailureException challengeDemandFailureException) {
            challengeDemandFailureException.build()
        }
    }

    ChallengeAnswerResult challengeAnswerForPasswordlessLogin(ChallengeAnswer input) {
        try {
            def credentials = authServerBridge.challengeAnswerForPasswordlessLogin(input.challengeId, input.challengeAnswer)
            new GenericCredentials(
                    username: JwtToken.fromString(credentials.accessToken, DecoderName.USERNAME).name,
                    credentials: credentials,
                    customer: customerBridge.myProfile(credentials.accessToken)
            )
        } catch (ChallengeAnswerFailureException challengeAnswerFailureException) {
            challengeAnswerFailureException.build()
        }
    }

    RefreshCredentialsResult refreshCredentials(RefreshCredentialsInput input) {
        try {
            def rawCredentials = authServerBridge.refreshToken(input.refreshToken)
            new RefreshCredentials(
                    accessToken: rawCredentials.accessToken,
                    refreshToken: rawCredentials.refreshToken
            )
        } catch (LoginFailureException loginException) {
            loginException.build()
        }
    }

    def passwordLogin(String email, String password, Site site) {
        authServerBridge.login(email, password, site)
    }

    SignInResult signIn(SignInInput signInInput) {
        try {
            def rawCredentials = customerBridge.signIn(signInInput)
            new GenericCredentials(
                    username: JwtToken.fromString(rawCredentials.credentials.access_token, DecoderName.USERNAME).name,
                    credentials: rawCredentials.credentials.toCredentials(),
                    customer: rawCredentials.customer
            )
        } catch (ConflictErrorException conflictErrorException) {
            SignInFailedReason.valueOf((String) conflictErrorException.innerResponse).build()
        } catch (BadRequestErrorException conflictErrorException) {
            SignInFailedReason.valueOf((String) conflictErrorException.innerResponse).build()
        }
    }

    PasswordlessSignUpResult passwordlessSignUp(PasswordlessSignUpInput passwordlessSignUpInput, DataFetchingEnvironment env) {
        try {
            def remoteAddress = DeviceIdentifierService.identifySource(env)
            customerBridge.passwordlessSignUp(passwordlessSignUpInput, remoteAddress)
        } catch (ConflictErrorException conflictErrorException) {
            PasswordlessSignUpFailedReason.valueOf((String) conflictErrorException.innerResponse).build()
        } catch (BadRequestErrorException conflictErrorException) {
            PasswordlessSignUpFailedReason.valueOf((String) conflictErrorException.innerResponse).build()
        }
    }


    UploadDocumentResult uploadVerificationDocument(Document documentInput) {
        try {
            def dataUrl = DataURL.from(documentInput.encodedFile)
            documentBridge.uploadDocument(documentInput.accessToken, dataUrl.decodedContent(), dataUrl.mediaType)
        } catch (NotAcceptableException notAcceptableException) {
            UploadDocumentReason.UNSUPPORTED_MEDIA_TYPE.build()
        }
    }


    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput) {
        try {
            customerBridge.updateProfile(customerUpdateInput)
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    CustomerUpdateResult updateProfileV2(CustomerUpdateInputV2 customerUpdateInput) {
        try {
            customerBridge.updateProfileV2(customerUpdateInput)
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    VerifyPhoneResult verifyPhone(VerifyPhoneInput verifyPhoneInput) {
        try {
            customerBridge.verifyPhone(verifyPhoneInput)
            Void.SUCCESS
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    PreferredAddressResult setPreferredAddress(PreferredAddressInput preferredAddressInput) {
        try {
            customerBridge.setPreferredAddress(preferredAddressInput)
            Void.SUCCESS
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    AddAddressResult addAddress(AddressInput addressInput) {
        try {
            customerBridge.addAddress(addressInput)
        }
        catch (BadRequestErrorException badRequestException) {
            AddAddressFailedReason.valueOf((String) badRequestException.innerResponse).build()
        }

    }

    UpdateAddressResult updateAddress(AddressInput addressInput) {
        try {
            customerBridge.updateAddress(addressInput)
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    DeleteAddressResult deleteAddress(AddressIdInput addressIdInput) {
        try {
            customerBridge.deleteAddress(addressIdInput)
            Void.SUCCESS
        } catch (BadRequestErrorException deleteAddressFailed) {
            DeleteAddressFailedReason.valueOf((String) deleteAddressFailed.innerResponse).build()
        }
    }

    def tokenLogin(String accessToken, String socialNetwork) {
        authServerBridge.socialLogin(accessToken, socialNetwork)
    }

    def webUserRegistration(String name, String surname, String username, String password, String repeatPassword) {
        authServerBridge.userRegistration(name, surname, username, password, repeatPassword)
    }

    ChangePasswordResult changePassword(ChangePasswordInput input) {
        try {
            authServerBridge.changePassword(input.currentPassword, input.newPassword, input.accessToken)
            Void.SUCCESS
        } catch (ChangePasswordException changePasswordException) {
            changePasswordException.build()
        }
    }

    Void resetPassword(ResetPasswordRequestInput input) {
        authServerBridge.resetPassword(input.username)
        Void.SUCCESS
    }


    ConfirmPasswordResult resetPasswordConfirm(ResetPasswordConfirmInput input) {
        try {
            authServerBridge.resetPasswordConfirm(input.token, input.password, input.user_id)
            Void.SUCCESS
        } catch (ConfirmPasswordException confirmPasswordException) {
            confirmPasswordException.build()
        }
    }

    Void disableUsername(UsernameInput input) {
        usersBridge.disableUsername(input)
        Void.SUCCESS
    }

    Void enableUsername(UsernameInput input) {
        usersBridge.enableUsername(input)
        Void.SUCCESS
    }

    Void enableWhatsApp(AccessTokenInput input) {
        customerBridge.enableWhatsApp(input)
    }

    Void disableWhatsApp(AccessTokenInput input) {
        customerBridge.disableWhatsApp(input)
    }

    Void userDevice(UserDeviceInput input) {
        customerBridge.userDevice(input)
    }

    Void deleteUserDevice(AccessTokenInput input) {
        customerBridge.deleteUserDevice(input)
    }

    OrderUpdateResult cancelOrder(CancelOrderInput cancelOrderInput) {
        try {
            orderBridge.cancel(cancelOrderInput)
        }
        catch (BadRequestErrorException ex) {
            OrderUpdateReason.valueOf((String) ex.innerResponse).build()
        }
        catch (EntityNotFoundException ex) {
            OrderUpdateReason.ORDER_NOT_FOUND.build()
        }
    }

    OrderUpdateResult cancelOrderReason(CancelOrderInput cancelOrderInput) {
        try {
            orderBridge.cancelReason(cancelOrderInput)
            Void.SUCCESS
        }
        catch (BadRequestErrorException ex) {
            OrderUpdateReason.INVALID_SUPPLIER_ORDERS_STATUS.build()
        }
        catch (EntityNotFoundException ex) {
            OrderUpdateReason.ORDER_NOT_FOUND.build()
        }
    }

    PlaceOrderResult placeOrder(PlaceOrderInput placeOrderInput) {
        try {
            orderBridge.placeOrder(placeOrderInput.accessToken, placeOrderInput.orders, placeOrderInput.wabiPayAccessToken, placeOrderInput.coupons)
            Void.SUCCESS
        }
        catch (BadRequestErrorException ex) {
            PlaceOrderFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    CustomerRateSupplierResult customerRateSupplier(CustomerRateSupplierInput customerRateSupplierInput) {
        try {
            customerBridge.customerRateSupplier(customerRateSupplierInput.accessToken, customerRateSupplierInput.supplierOrderId, customerRateSupplierInput.supplierId, customerRateSupplierInput.opinion, customerRateSupplierInput.score)
        }
        catch (BadRequestErrorException ex) {
            CustomerRateSupplierFailedReason.valueOf((String) ex.innerResponse).build()
        }
        catch (ConflictErrorException ex) {
            CustomerRateSupplierFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    CustomerReportRateResult customerReportRate(CustomerReportRateInput customerReportRateInput) {
        try {
            customerBridge.customerReportRate(customerReportRateInput.accessToken, customerReportRateInput.rateId)
        } catch (EntityNotFoundException ex) {
            CustomerReportRateFailedReason.RATE_NOT_FOUND.build()
        }
    }

    Void markSuggestionAsRead(MarkSuggestionInput input) {
        customerBridge.markSuggestionAsRead(input.accessToken, input.supplierIds)
    }

    Void acceptTc(AcceptTcInput input) {
        customerBridge.acceptTc(input)
    }

    Boolean markProductAsFavorite(FavoriteProductInput favoriteProductInput) {
        recommendedOrderBridge.markProductAsFavorite(favoriteProductInput)
    }

    Boolean unmarkFavoriteProduct(FavoriteProductInput favoriteProductInput) {
        recommendedOrderBridge.unmarkFavoriteProduct(favoriteProductInput)
    }

    private LoginResult resolveCredentialsResponse(Credentials credentials, Boolean deviceSupportLegacy) {

        List<String> authorities = JwtToken.authorities(credentials.accessToken)
        if (authorities.contains("FE_MIGRATE_TO_PASSWORDLESS")) {
            return new UpgradeRequired(
                    credentials: credentials
            )
        }

        def decodedUsername = JwtToken.fromString(credentials.accessToken, DecoderName.USERNAME)
        String country = JwtToken.countryFromString(credentials.accessToken)

        if (countrySupportLegacy(country) && deviceSupportLegacy) {
            return new LegacyCredentials(
                    username: decodedUsername.name,
                    credentials: credentials
            )
        }
        return new GenericCredentials(
                username: decodedUsername.name,
                credentials: credentials,
                customer: customerBridge.myProfile(credentials.accessToken)
        )
    }

    private Boolean countrySupportLegacy(String country) {
        return emptyListDisableCountryCheck() || legacyCountries.contains(country)
    }

    private Boolean emptyListDisableCountryCheck() {
        return legacyCountries.isEmpty() && isQA()
    }

    private Boolean isQA() {
        return environment == "qa"
    }
}
