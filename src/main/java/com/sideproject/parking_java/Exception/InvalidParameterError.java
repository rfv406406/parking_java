package com.sideproject.parking_java.Exception;

import ch.qos.logback.core.status.Status;

public class InvalidParameterError extends ServiceException{
    public InvalidParameterError(String message) {
        super(StatusCode.Base.INVALID_PARAMETER, message);
    }
}
