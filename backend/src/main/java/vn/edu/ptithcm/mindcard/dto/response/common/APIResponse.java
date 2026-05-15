package vn.edu.ptithcm.mindcard.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;

import lombok.Getter;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import java.time.Instant;

import lombok.Builder;


/**
 * Any null field will be ignored when serializing to JSON
 */
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter

public class APIResponse<T> {

    @Builder.Default
    private final boolean success = true;

    private final T data;

    private final PaginationMeta meta;

    private final String message;

    private final String errorCode;

    private final Object errorDetails;

    @Builder.Default
    private final long timestamp = Instant.now().toEpochMilli();


    public static APIResponse<Void> ok() {
        return APIResponse.<Void>builder().success(true).build();
    }
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

    public static APIResponse<Void> error(ErrorCode error){
        return APIResponse.<Void>builder()
                .success(false)
                .errorCode(error.getCode())
                .build();
    }

    public static APIResponse<Void> error(ErrorCode error, String message){
        return APIResponse.<Void>builder()
                .success(false)
                .errorCode(error.getCode())
                .message(message)
                .build();
    }

    public static APIResponse<Void> error(ErrorCode error, Object errorDetails){
        return APIResponse.<Void>builder()
                .success(false)
                .errorCode(error.getCode())
                .errorDetails(errorDetails)
                .build();
    }

}