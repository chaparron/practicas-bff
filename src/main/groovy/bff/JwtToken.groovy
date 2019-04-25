package bff

import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.transform.InheritConstructors

class JwtToken {
    String username

    static JwtToken fromString(String token) {
        def fields = token.split('\\.')
        if (fields.length != 3) throw new InvalidToken()

        try {
            def username = new JsonSlurper().parse(
                    Base64.decoder.decode(fields[1])
            )['user_name'].toString()
            new JwtToken(username: username)
        } catch (IllegalArgumentException | JsonException e) {
            throw new InvalidToken('Invalid token', e)
        }
    }
}

@InheritConstructors
class InvalidToken extends RuntimeException{
    InvalidToken() {
        super("Invalid token")
    }
}
