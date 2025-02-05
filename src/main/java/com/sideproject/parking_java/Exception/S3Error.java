package com.sideproject.parking_java.Exception;

public class S3Error extends ServiceException {
    public S3Error(String message) {
        super(StatusCode.Base.S3_ERROR, message);
    }
}
