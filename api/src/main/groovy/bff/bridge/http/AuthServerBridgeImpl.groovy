package bff.bridge.http

import bff.bridge.AuthServerBridge
import bff.configuration.AccessToBackendDeniedException
import bff.configuration.BadRequestErrorException
import bff.configuration.BridgeHttpServerErrorException
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

            //TODO: terminar el mapping del error en este caso
        } catch (BadRequestErrorException badRequestErrorException) {
            LoginFailureReason.valueOf((String)badRequestErrorException.innerResponse).doThrow()
            badRequestErrorException.printStackTrace()
        }
    }

    @Override
    Challenge challengeRequestForChangeToPasswordlessAuthentication(String countryCode, String phone, String accessToken, String remoteAddress){
        def uri = UriComponentsBuilder.fromUri(root.resolve("/user/passwordless/authswitch/challenge")).toUriString().toURI()
        try{
            http.exchange(
                    RequestEntity.method(HttpMethod.POST, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                            .header("Address-Received-In-Bff", remoteAddress)
                            .body([countryCode : countryCode, phone : phone])
                    , Challenge).body
        }catch(AccessToBackendDeniedException accessToBackendDeniedException){
            SignedChallengeDemandFailureReason.valueOf((String)accessToBackendDeniedException.cause.statusCode.name()).doThrow()
            throw accessToBackendDeniedException
        }catch (BadRequestErrorException badRequestErrorException) {
            String innerResponse = (String)badRequestErrorException.innerResponse
            if ("TOO_MANY_SHIPMENTS" == innerResponse){
                int waitTime = ((BridgeHttpServerErrorException)badRequestErrorException.cause).responseHeaders.getFirst("wait-time").toInteger()
                throw new TooManyShipmentsException(waitTime)
            }
            SignedChallengeDemandFailureReason.valueOf(innerResponse).doThrow()
            badRequestErrorException.printStackTrace()
        }
    }

    @Override
    Credentials challengeAnswerForChangeToPasswordlessAuthentication(String challengeId, String challengeAnswer, String accessToken){
        def uri = UriComponentsBuilder.fromUri(root.resolve("/user/passwordless/authswitch/answer")).toUriString().toURI()
        try{
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.POST, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                            .body([challengeId : challengeId, challengeAnswer : challengeAnswer])
                    , Map).body
            mapCredentials body
        }catch(AccessToBackendDeniedException accessToBackendDeniedException){
            ChallengeAnswerFailureReason.valueOf((String)accessToBackendDeniedException.cause.statusCode.name()).doThrow()
            throw accessToBackendDeniedException
        }catch (BadRequestErrorException badRequestErrorException) {
            ChallengeAnswerFailureReason.valueOf((String)badRequestErrorException.innerResponse).doThrow()
            badRequestErrorException.printStackTrace()
        }
    }

    @Override
    Challenge challengeRequestForPasswordlessLogin(String countryCode, String phone, String remoteAddress){
        def uri = UriComponentsBuilder.fromUri(root.resolve("/user/passwordless/login/challenge")).toUriString().toURI()
        try{
            http.exchange(
                    RequestEntity.method(HttpMethod.POST, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Address-Received-In-Bff", remoteAddress)
                            .body([countryCode : countryCode, phone : phone])
                    , Challenge).body
        }catch(AccessToBackendDeniedException accessToBackendDeniedException){
            ChallengeDemandFailureReason.valueOf((String)accessToBackendDeniedException.cause.statusCode.name()).doThrow()
            throw accessToBackendDeniedException
        }catch (BadRequestErrorException badRequestErrorException) {
            String innerResponse = (String)badRequestErrorException.innerResponse
            if ("TOO_MANY_SHIPMENTS" == innerResponse){
                int waitTime = ((BridgeHttpServerErrorException)badRequestErrorException.cause).responseHeaders.getFirst("wait-time").toInteger()
                throw new TooManyShipmentsException(waitTime)
            }
            ChallengeDemandFailureReason.valueOf((String)badRequestErrorException.innerResponse).doThrow()
            badRequestErrorException.printStackTrace()
        }
    }

    @Override
    Credentials challengeAnswerForPasswordlessLogin(String challengeId, String challengeAnswer){
        def uri = UriComponentsBuilder.fromUri(root.resolve("/user/passwordless/login/answer")).toUriString().toURI()
        try{
            def body = http.exchange(
                    RequestEntity.method(HttpMethod.POST, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body([challengeId : challengeId, challengeAnswer : challengeAnswer])
                    , Map).body
            mapCredentials body
        }catch(AccessToBackendDeniedException accessToBackendDeniedException){
            ChallengeAnswerFailureReason.valueOf((String)accessToBackendDeniedException.cause.statusCode.name()).doThrow()
            throw accessToBackendDeniedException
        }catch (BadRequestErrorException badRequestErrorException) {
            ChallengeAnswerFailureReason.valueOf((String)badRequestErrorException.innerResponse).doThrow()
            badRequestErrorException.printStackTrace()
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
    Boolean resetPassword(String username) {
        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.POST, root.resolve('/user/password/reset/request'))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                            [
                                    username: username
                            ]
                    )
                    , Boolean).body
        } catch (BadRequestErrorException exception) {
            throw new SilentException((String) exception.innerResponse)
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
        } catch (BadRequestErrorException exception) {
            ConfirmPasswordReason.valueOf((String)exception.innerResponse).doThrow()
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
        } catch (BadRequestErrorException exception) {
            ChangePasswordReason.valueOf((String)exception.innerResponse).doThrow()
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