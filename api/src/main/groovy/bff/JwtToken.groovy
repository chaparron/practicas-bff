package bff

import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.transform.InheritConstructors

import static java.util.Base64.getUrlDecoder

class JwtToken {
    String name

    static JwtToken fromString(String token, DecoderName decoderName) {
        def fields = token.split('\\.')
        if (fields.length != 3) throw new InvalidToken()

        try {
            def decode = new JsonSlurper().parse(
                    getUrlDecoder().decode(fields[1])
            )[decoderName.getDecoderName()].toString()
            new JwtToken(name: decode)
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

enum DecoderName {
    USERNAME("username"),
    ENTITY_ID("entityId")

    private final String decoder

    DecoderName(String decoderName) {
        this.decoder = decoderName
    }

    String getDecoderName() {
        decoder
    }
}
