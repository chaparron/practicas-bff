package bff.model

import bff.JwtToken
import bff.bridge.*

import com.coxautodev.graphql.tools.GraphQLMutationResolver

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

    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput) {
        try {
            customerBridge.updateProfile(customerUpdateInput)
        } catch(CustomerUpdateException customerUpdateException) {
            customerUpdateException.build()
        }
    }

    VerifyPhoneResult verifyPhone(VerifyPhoneInput verifyPhoneInput) {
        try {
            customerBridge.verifyPhone(verifyPhoneInput)
            Void.SUCCESS
        } catch (VerifyExpiredException verifyExpiredException) {
            verifyExpiredException.build()
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
}
