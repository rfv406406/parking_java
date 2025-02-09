package com.sideproject.parking_java.Exception;

import java.time.LocalDateTime;

import com.sideproject.parking_java.Exception.StatusCode.interfaceStatusCode;

public class ServiceException extends RuntimeException {

    public ServiceException(interfaceStatusCode statusCode, String message) {
        super(buildErrorMessage(statusCode.getErrorName(), message, LocalDateTime.now()));
    }

    public static String buildErrorMessage(String statusCode, String message, LocalDateTime errorTime) {
        return "Error status: " + statusCode + "\n" + "Error message: " + message + "\n" + "Error time: " + errorTime;
    }
}
