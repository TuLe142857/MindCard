package vn.edu.ptithcm.mindcard.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import vn.edu.ptithcm.mindcard.common.exception.ErrorCode;
import java.time.Instant;
import lombok.Getter;
import lombok.Builder;


/**
 * Any null field will be ignored when serializing to JSON
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final String errorCode;
    private final long timestamp;


    public static <T> APIResponse<T> ok(T data){
        return APIResponse.<T>builder()
                .data(data)
                .timestamp(Instant.now().getEpochSecond())
                .build();
    }

    public static <T> APIResponse<T> ok(T data, String message){
        return APIResponse.<T>builder()
                .data(data)
                .message(message)
                .timestamp(Instant.now().getEpochSecond())
                .build();
    }

    public static <T> APIResponse<T> error(ErrorCode error){
        return APIResponse.<T>builder()
                .errorCode(error.getCode())
                .timestamp(Instant.now().getEpochSecond())
                .build();
    }

    public static <T> APIResponse<T> error(ErrorCode error, String message){
        return APIResponse.<T>builder()
                .errorCode(error.getCode())
                .message(message)
                .timestamp(Instant.now().getEpochSecond())
                .build();
    }
}