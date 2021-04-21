package bff.bridge.http

import bff.bridge.DocumentBridge
import bff.configuration.NotAcceptableException
import bff.model.UploadDocumentResult
import bff.model.UploadedDocument
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations

class DocumentBridgeImpl implements DocumentBridge{
    URI root
    RestOperations http

    @Override
    UploadDocumentResult uploadDocument(String accessToken, byte[] content, MediaType contentType) throws NotAcceptableException {
        def body = http.exchange(
                RequestEntity.method(HttpMethod.POST, root.resolve('/resource/uploadDocument'))
                        .contentType(contentType)
                        .contentLength(content.length)
                        .body(content)
                , UploadedDocument
        ).body

        body
        new UploadedDocument(
                id: body.id
        )
    }
}
