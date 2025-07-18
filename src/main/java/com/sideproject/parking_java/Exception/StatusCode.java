package com.sideproject.parking_java.exception;

public class StatusCode {

    public interface interfaceStatusCode {
        String getErrorName();
    };

    // @AllArgsConstructor
    // @Getter
    public enum Base implements interfaceStatusCode{
        INTERNAL_SERVER_ERROR,
        INVALID_PARAMETER,
        S3_ERROR,
        DATABASE_ERROR,
        AUTHENTICATION_ERROR;

        @Override
        public String getErrorName() {
            return this.name();
        }
    }
}
