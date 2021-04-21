package bff.model.utils

import org.springframework.http.MediaType

class DataURL {

    String data
    MediaType mediaType

    static DataURL from(String data) {
        def fields = data.split(',')
        def contentType = MediaType.parseMediaType(fields[0] - 'data:' - ';base64')
        def content = fields[1]

        new DataURL(data: content, mediaType: contentType)
    }

    byte[] decodedContent() {
        data.decodeBase64()
    }
}
