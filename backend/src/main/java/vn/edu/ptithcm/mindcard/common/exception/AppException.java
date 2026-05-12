package vn.edu.ptithcm.mindcard.common.exception;

public class AppException extends RuntimeException{
    private final  ErrorCode errorCode;

    public AppException(){
        super();
        this.errorCode = ErrorCode.SERVER_ERROR;
    }

    public AppException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
