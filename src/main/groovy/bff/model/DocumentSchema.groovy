package bff.model

interface UploadDocumentResult {}


enum UploadDocumentReason {
    UPLOAD_DOCUMENT_FAILED

    def build() {
        return new UploadDocumentFailed(reason: this)
    }
}

class UploadDocumentFailed implements UploadDocumentResult {
    UploadDocumentReason reason
}

class UploadedDocument implements UploadDocumentResult {
    String id
}

class Document {
    String encodedFile
    String accessToken
}