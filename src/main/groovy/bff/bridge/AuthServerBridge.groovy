package bff.bridge

import bff.model.AuthType
import bff.model.Credentials
import bff.model.ProfileCredentialsResult

interface AuthServerBridge {

    Credentials login(String email, String password, AuthType authType)

    Credentials refreshToken(String refreshToken)

    Credentials userRegistration(String name, String surname, String username, String password, String repeatPassword)

    void resetPassword(String username)

    void resetPasswordConfirm(String token, String password, Long user_id)


    void changePassword(String oldPassword, String newPassword, String accessToken)

    ProfileCredentialsResult updateProfile(Long id, String firstName, String lastName,
                                           String username, String document, String dob,
                                           String areaCode, String phone, String gender,
                                           String seller, String sellerType, String address, String accessToken)

    void completeProfile(String phone, String document, String address, String accsstoken,
                         String recaptcha)
}

