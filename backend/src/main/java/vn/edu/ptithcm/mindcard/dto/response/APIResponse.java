package vn.edu.ptithcm.mindcard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
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

    @Builder.Default
    private final boolean success = true;

    private final T data;

    private final PaginationMeta meta;

    private final String message;

    private final String errorCode;

    @Builder.Default
    private final long timestamp = Instant.now().getEpochSecond();

    public static <T> APIResponse<T> ok(T data){
        return APIResponse.<T>builder()
                .data(data)
                .build();
    }

    public static <T> APIResponse<T> ok(T data, String message){
        return APIResponse.<T>builder()
                .data(data)
                .message(message)
                .build();
    }

    public static <T> APIResponse<T> ok(T data, PaginationMeta meta){
        return APIResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(meta)
                .build();
    }

    public static <T> APIResponse<T> ok(T data, PaginationMeta meta, String message){
        return APIResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(meta)
                .message(message)
                .build();
    }

    public static <T> APIResponse<T> error(ErrorCode error){
        return APIResponse.<T>builder()
                .errorCode(error.getCode())
                .build();
    }

    public static <T> APIResponse<T> error(ErrorCode error, String message){
        return APIResponse.<T>builder()
                .errorCode(error.getCode())
                .message(message)
                .build();
    }
}