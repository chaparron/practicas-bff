package bff.bridge

import bff.model.Credentials
import bff.model.Site

interface AuthServerBridge {

    Credentials login(String email, String password, Site site)

    Credentials refreshToken(String refreshToken)

    Credentials userRegistration(String name, String surname, String username, String password, String repeatPassword)

    void resetPassword(String username)

    def resetPasswordConfirm(String token, String password, Long user_id)


    void changePassword(String oldPassword, String newPassword, String accessToken)

    void completeProfile(String phone, String document, String address, String accsstoken,
                         String recaptcha)
}
