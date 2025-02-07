package com.sideproject.parking_java.Exception;

public class InvalidParameterError extends ServiceException{
    public InvalidParameterError(String message) {
        super(StatusCode.Base.INVALID_PARAMETER, message);
    }
}
