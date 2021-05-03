package bff.model

class CredentialsResponse {
    String access_token
    String refresh_token
    String token_type
    String expires_in
    String scope

    Credentials toCredentials() {
        new Credentials(
                accessToken: access_token,
                refreshToken: refresh_token,
                tokenType: token_type,
                expiresIn: expires_in,
                scope: scope
        )

    }

}

class CredentialsCustomerResponse {
    CredentialsResponse credentials
    Customer customer
}
