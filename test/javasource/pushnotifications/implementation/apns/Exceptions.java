package pushnotifications.implementation.apns;

class DeviceTokenInvalidException extends Exception {
	
	private static final long serialVersionUID = -8101144149147746865L;

	DeviceTokenInvalidException(String message) {
        super(message);
    }
}

class MessageRejectedException extends Exception {
	
	private static final long serialVersionUID = -2645648758525554011L;
	
	MessageRejectedException(String message) {
        super(message);
    }
}

class NoAuthenticationTypeException extends Exception {
	
	private static final long serialVersionUID = 3983717197482897278L;
	
}
