package com.sideproject.parking_java.Exception;

import ch.qos.logback.core.status.Status;

public class DatabaseError extends ServiceException{
    public DatabaseError(String message) {
        super(StatusCode.Base.DATABASE_ERROR, message);
    }
}
