package bff.model

import groovy.transform.InheritConstructors;

@InheritConstructors
class Unauthorized extends RuntimeException {}


@InheritConstructors
class InvalidPassword extends RuntimeException {}


@InheritConstructors
class CurrentPasswordMismatch extends RuntimeException {}


@InheritConstructors
class UsernameRegistrationException extends RuntimeException {
    UsernameRegistrationReason reason
    def build() {
        new UsernameRegistrationFailed( reason : this.reason)
    }
}

class LoginFailureException extends RuntimeException {
    LoginFailureReason loginFailureReason
    def build() {
        new LoginFailed(reason: this.loginFailureReason)
    }
}


class UpdateProfileException extends RuntimeException {
    UpdateProfileReason reason
    def build() {
        new UpdateProfileFailed(reason: this.reason)
    }
}

class WebRegisterException extends RuntimeException {
    RegisterFailureReason registerReason
    def build() {
        new RegisterFailed(registerReason: this.registerReason)
    }
}