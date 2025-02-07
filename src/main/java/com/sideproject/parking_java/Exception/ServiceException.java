package com.sideproject.parking_java.Exception;

import java.time.LocalDateTime;

public class ServiceException extends RuntimeException {

    public ServiceException(Enum<?> statusCode, String message) {
        super(buildErrorMessage(statusCode.name(), message, LocalDateTime.now()));
    }

    public static String buildErrorMessage(String statusCode, String message, LocalDateTime errorTime) {
        return "Error status: " + statusCode + "\n" + "Error message: " + message + "\n" + "Error time: " + errorTime;
    }
}
