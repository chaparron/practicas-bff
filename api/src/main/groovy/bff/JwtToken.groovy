package bff

import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.transform.InheritConstructors

import static java.util.Base64.getUrlDecoder

class JwtToken {
    String name

    static JwtToken fromString(String token, DecoderName decoderName) {
        try {
            def decode = getTokenMapFromRawToken(token)[decoderName.getDecoderName()].toString()
            new JwtToken(name: decode)
        } catch (IllegalArgumentException | JsonException e) {
            throw new InvalidToken('Invalid token', e)
        }
    }

    static String countryFromString(String token) {
        try {
            getTokenMapFromRawToken(token)["user"]["countries"].first()["id"].toString()
        } catch (IllegalArgumentException | JsonException e) {
            throw new InvalidToken('Invalid token', e)
        }
    }

    static String userIdFromToken(String token) {
        try {
            getTokenMapFromRawToken(token)["user"]["id"].toString()
        } catch (IllegalArgumentException | JsonException e) {
            throw new InvalidToken('Invalid token', e)
        }
    }

    static List<String> authorities(String token) {
        try {
            getTokenMapFromRawToken(token)["authorities"] as List<String>
        } catch (IllegalArgumentException | JsonException e) {
            throw new InvalidToken('Invalid token', e)
        }
    }

    private static def getTokenMapFromRawToken(String token){
        def fields = token.split('\\.')
        if (fields.length != 3) throw new InvalidToken()
        return new JsonSlurper().parse(
                getUrlDecoder().decode(fields[1]))
    }
}

@InheritConstructors
class InvalidToken extends RuntimeException {
    InvalidToken() {
        super("Invalid token")
    }
}

enum DecoderName {
    USERNAME("username"),
    ENTITY_ID("entityId"),
    USER("user")

    private final String decoder

    DecoderName(String decoderName) {
        this.decoder = decoderName
    }

    String getDecoderName() {
        decoder
    }
}
