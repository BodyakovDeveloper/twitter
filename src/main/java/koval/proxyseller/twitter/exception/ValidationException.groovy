package koval.proxyseller.twitter.exception

class ValidationException extends RuntimeException {
    ValidationException(String message) {
        super(message)
    }

    ValidationException(String message, Throwable cause) {
        super(message, cause)
    }
}

