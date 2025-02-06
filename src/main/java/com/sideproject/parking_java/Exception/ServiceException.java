package com.sideproject.parking_java.Exception;

public class ServiceException extends RuntimeException {
    public ServiceException(Enum<?> statusCode, String message) {
        super(buildErrorMessage(statusCode.name(), message));
    }

    public static String buildErrorMessage(String statusCode, String message) {
        return "Error status: " + statusCode + "\n" + "Error message: " + message;
    }
}
