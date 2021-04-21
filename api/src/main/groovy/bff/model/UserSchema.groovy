package bff.model

import groovy.transform.ToString

interface LoginResult {}

interface RefreshCredentialsResult {}

interface ChangePasswordResult {}

interface ConfirmPasswordResult {}

class User {
    Long id
    String username
    String firstName
    String lastName
    Boolean acceptWhatsApp
    String phone
    UserCredentials credentials
}

class UserCredentials {
    Boolean enabled
}

class LoginInput {
    String username
    String password
    Site site
}

@ToString()
class RefreshCredentials implements RefreshCredentialsResult {
    String accessToken
    String refreshToken
}

@ToString()
class Credentials {
    String accessToken
    String refreshToken
    String tokenType
    String scope
    String expiresIn
}

class GenericCredentials implements LoginResult , SignInResult{
    String username
    Credentials credentials
}


//TODO: Verificar  si es necesario el retorno del site para el tipo de web que lo este pidiendo.
enum Site {
    CUSTOMER("FE_WEB"),
    SUPPLIER("SUPPLIER_WEB"),
    MANUFACTURER("MANUFACTURER_WEB"),
    BO("BO_WEB")

    String permission

    Site(String permission) {
        this.permission = permission
    }

    static Site fromPermission(String permission){
        Site.values().find({it.permission == permission})
    }
}

enum LoginFailureReason {
    FORBIDDEN,
    UNAUTHORIZED

    def doThrow() {
        throw new LoginFailureException(loginFailureReason: this)
    }
}

enum AuthType {
    USER,
    ADMIN
}


enum ChangePasswordReason {
    PASSWORD_MISMATCH

    def doThrow() {
        throw new ChangePasswordException(changePasswordReason: this)
    }

}

enum ConfirmPasswordReason {
    TOKEN_EXPIRED

    def doThrow() {
        throw new ConfirmPasswordException(confirmPasswordReason: this)
    }

}

class LoginFailed implements LoginResult , RefreshCredentialsResult {
    LoginFailureReason reason
}

class ChangePasswordFailed implements ChangePasswordResult {
    ChangePasswordReason reason
}

class ConfirmPasswordFailed implements ConfirmPasswordResult{
    ConfirmPasswordReason confirmPasswordReason
}

class RefreshCredentialsInput {
    String refreshToken
}

class ResetPasswordRequestInput {
    String username
}
class ResetPasswordConfirmInput {
    String token
    String password
    Long user_id
}

class ChangePasswordInput {
    String currentPassword
    String newPassword
    String accessToken
}

class PhoneInput {
    String phone
}










