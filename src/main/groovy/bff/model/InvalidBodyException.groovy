package bff.model

class InvalidBodyException extends  RuntimeException {

    def error

    InvalidBodyException(def p) {
        this.error = p
    }
}
