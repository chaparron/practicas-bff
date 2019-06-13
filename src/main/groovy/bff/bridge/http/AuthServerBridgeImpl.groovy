package bff.bridge.http

import bff.bridge.AuthServerBridge
import bff.configuration.AccessToBackendDeniedException
import bff.configuration.BadRequestErrorException
import bff.model.*
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

@Slf4j
class AuthServerBridgeImpl implements AuthServerBridge {

    URI root
    RestOperations http

    @Override
    Credentials login(String email, String password, Site site) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/user/${site.name()}/login")).toUriString().toURI()
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.POST, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                            [
                                    username : email,
                                    password : password
                            ]
                    )
                    , Map).body

            mapCredentials body
        } catch (AccessToBackendDeniedException accessToBackendDeniedException) {
            mapperLoginException(accessToBackendDeniedException.cause.statusCode.name())
        }
    }


    @Override
    Credentials refreshToken(String refreshToken) {
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve('/user/refresh'))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                            [
                                    refresh_token: refreshToken
                            ]
                    )
                    , Map).body
            mapCredentials body
        } catch (AccessToBackendDeniedException accessToBackendDeniedException) {
            mapperLoginException(accessToBackendDeniedException.cause.statusCode.name())
        }
    }

    @Override
    Credentials userRegistration(String name, String surname, String username, String password, String repeatPassword) {
        try {
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve('/user/register'))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                            [
                                    name: name,
                                    surname: surname,
                                    username: username,
                                    password: password,
                                    repeatPassword: repeatPassword
                            ]
                    )
                    , Map).body

            mapCredentials(body)

        } catch (RestClientException e) {
            def body = e.responseBody
            if (body && body.error) {
                mapUserRegistrationException(body.error[0])
            }
            throw new RuntimeException("failed to update user profile", e)
        }
    }

    @Override
    void resetPassword(String username) {
        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve('/user/password/reset/request'))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                            [
                                    username: username
                            ]
                    )
                    , Map)
        } catch (RestClientException e) {
            throw new RuntimeException("failed to reset password", e)
        }
    }

    @Override
    def resetPasswordConfirm(String token, String password, Long user_id) {
        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve('/user/password/reset/confirm'))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                            [
                                    token   : token,
                                    password: password,
                                    user_id : user_id
                            ]
                    ), Map
            )
        } catch (BadRequestErrorException e) {
            ConfirmPasswordReason.TOKEN_EXPIRED.doThrow()
        }
    }

    @Override
    void changePassword(String currentPassword, String newPassword, String accessToken) {
        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve('/user/password'))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                            .body(
                            [
                                    current_password: currentPassword,
                                    new_password    : newPassword
                            ]
                    ), Map
            )
        } catch (BadRequestErrorException b) {
            ChangePasswordReason.PASSWORD_MISMATCH.doThrow()
        }
    }

    void completeProfile(String phone, String document, String address, String accessToken,
                         String recaptcha) {

        http.exchange(
                RequestEntity.method(HttpMethod.POST, root.resolve('/user/profile/complete'))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .body(
                        [
                                document   : document,
                                phone      : phone,
                                address    : address,
                                recaptcha  : recaptcha
                        ]
                )
                , Map).body
    }

    def private profileCredentials(def body) {
        new ProfileCredentials(accessToken: body.accessToken)
    }

    def private mapUserRegistrationException(def error) {
        RegisterFailureReason.valueOf(error)?.doThrow()

        throw new RuntimeException("User-Registration: Not implemented: ${new JsonBuilder(error)}")
    }

    def private static mapperLoginException(def error) {
        LoginFailureReason.valueOf(error)?.doThrow()

        throw new RuntimeException("User-Registration: Not implemented: ${new JsonBuilder(error)}")
    }

    def mapCredentials(body) {
        new Credentials(
                accessToken: body.access_token,
                refreshToken: body.refresh_token,
                tokenType: body.token_type,
                scope: body.scope,
                expiresIn: body.expires_in
        )
    }
}