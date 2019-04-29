package bff.model

import bff.JwtToken
import bff.bridge.*
import bff.model.utils.DataURL

import com.coxautodev.graphql.tools.GraphQLMutationResolver

import groovy.util.logging.Slf4j

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class Mutation implements GraphQLMutationResolver {

    @Autowired
    AuthServerBridge authServerBridge

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

    Void resetPasswordConfirm(ResetPasswordConfirmInput input) {
        authServerBridge.resetPasswordConfirm(input.token, input.password, input.user_id)
        Void.SUCCESS
    }

    ChangePasswordResult changePassword(ChangePasswordInput input) {
        try {
            authServerBridge.changePassword(input.currentPassword, input.newPassword, input.accessToken)
            Void.SUCCESS
        } catch (CurrentPasswordMismatch e) {
            return ChangePasswordFailed.INCORRECT_PASSWORD
        } catch (InvalidPassword e) {
            return ChangePasswordFailed.INVALID_NEW
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

    ProfileCredentialsResult updateProfile(UserProfileInput input) {
        try {
            def profile = authServerBridge.updateProfile(input.id,
                    input.firstName, input.lastName,
                    input.username, input.document,
                    input.dob, input.areaCode,
                    input.phone, input.gender.toString(),
                    input.seller, input.seller_type,
                    input.address,
                    input.accessToken)

            if (profile == null) {
                return Void.SUCCESS
            } else {
                return profile
            }
        } catch (UpdateProfileException updateException) {
            return updateException.build()
        }
    }
}
