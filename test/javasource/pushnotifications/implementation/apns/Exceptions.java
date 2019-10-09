package pushnotifications.implementation.apns;

class DeviceTokenInvalidException extends Exception {
    DeviceTokenInvalidException(String message) {
        super(message);
    }
}

class MessageRejectedException extends Exception {
    MessageRejectedException(String message) {
        super(message);
    }
}

class NoAuthenticationTypeException extends Exception {
}
