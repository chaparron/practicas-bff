package bff.bridge

import bff.model.UploadDocumentResult
import org.springframework.http.MediaType

interface DocumentBridge {
    UploadDocumentResult uploadDocument(String accessToken, byte[] content, MediaType contentType)
}