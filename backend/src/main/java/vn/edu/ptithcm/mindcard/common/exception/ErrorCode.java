package vn.edu.ptithcm.mindcard.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SERVER_ERROR("SERVER_ERROR", 500),
    NOT_FOUND("NOT_FOUND", 404);

    private final String code;
    private final int httpStatusCode;

    ErrorCode(String code, int httpStatusCode) {
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

}
