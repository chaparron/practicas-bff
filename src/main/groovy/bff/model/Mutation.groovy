package bff.model

import bff.JwtToken
import bff.bridge.AuthServerBridge
import bff.bridge.CustomerBridge
import bff.bridge.OrderBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.ConflictErrorException
import bff.configuration.EntityNotFoundException
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import graphql.GraphQLError
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
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

    LoginResult login(LoginInput input) {
        try {
            def credentials = passwordLogin(input.username, input.password, input.site)
            new GenericCredentials(
                username: JwtToken.fromString(credentials.accessToken).username,
                credentials: credentials
            )
        } catch (LoginFailureException loginException) {
            loginException.build()
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
                    username: JwtToken.fromString(rawCredentials.accessToken).username,
                    credentials: rawCredentials
            )

        } catch(ConflictErrorException conflictErrorException) {
            SignInFailedReason.valueOf((String) conflictErrorException.innerResponse).build()
        }
    }



    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput) {
        try {
            customerBridge.updateProfile(customerUpdateInput)
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
            Void.SUCCESS
        } catch (CustomerException customerException) {
            customerException.build()
        }
    }

    DeleteAddressResult deleteAddress(AddressIdInput addressIdInput) {
        try {
            customerBridge.deleteAddress(addressIdInput)
            Void.SUCCESS
        } catch (BadRequestErrorException deleteAddressFailed) {
            DeleteAddressFailedReason.valueOf( (String)  deleteAddressFailed.innerResponse).build()
        }
    }

    def tokenLogin(String accessToken, String socialNetwork) {
        authServerBridge.socialLogin(accessToken, socialNetwork)
    }

    def webUserRegistration(String name, String surname, String username, String password, String repeatPassword) {
        authServerBridge.userRegistration(name, surname, username, password, repeatPassword)
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

    ChangePasswordResult changePassword(ChangePasswordInput input) {
        try {
            authServerBridge.changePassword(input.currentPassword, input.newPassword, input.accessToken)
            Void.SUCCESS
        } catch (ChangePasswordException changePasswordException) {
            changePasswordException.build()
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

    OrderUpdateResult cancelOrder(CancelOrderInput cancelOrderInput) {
        try {
            orderBridge.cancel(cancelOrderInput)
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
            orderBridge.placeOrder(placeOrderInput.accessToken, placeOrderInput.orders)
            Void.SUCCESS
        }
        catch (BadRequestErrorException ex) {
            PlaceOrderFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }
}
