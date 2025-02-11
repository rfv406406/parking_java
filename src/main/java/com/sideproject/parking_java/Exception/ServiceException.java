package com.sideproject.parking_java.Exception;

import java.util.Date;

import com.sideproject.parking_java.Exception.StatusCode.interfaceStatusCode;

public class ServiceException extends RuntimeException {

    public ServiceException(interfaceStatusCode statusCode, String message) {
        super(buildErrorMessage(statusCode.getErrorName(), message, new Date()));
    }

    public static String buildErrorMessage(String statusCode, String message, Date errorTime) {
        return "Error status: " + statusCode + "\n" + "Error message: " + message + "\n" + "Error time: " + errorTime;
    }
}
