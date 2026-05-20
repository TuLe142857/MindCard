package vn.edu.ptithcm.mindcard.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SERVER_ERROR(500),
    NOT_FOUND(404),
    VALIDATION_ERROR(401),
    LOGIN_FAILED(400),

    UNAUTHENTICATED(403),
    USER_NOT_FOUND(404),

    FORBIDDEN(400),

    JWT_TOKEN_EXPIRED(400),
    INVALID_JWT_TOKEN(400),
    JWT_TOKEN_REVOKED(400),

    RESOURCE_ALREADY_EXIST(409),
    RESOURCE_NOT_FOUND(404),
    INVALID_OTP(400),

    FILE_UPLOAD_FAILED(400),

    ACTION_ALREADY_PERFORMED(400)
    ;

    /** ErrorCode as String */
    private final String code;

    /** Http status code for API response */
    private final int httpStatusCode;

    ErrorCode(String code, int httpStatusCode) {
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    ErrorCode(int httpStatusCode){
        this.code = name();
        this.httpStatusCode = httpStatusCode;
    }

}
