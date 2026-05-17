package vn.edu.ptithcm.mindcard.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import java.time.Instant;
import java.util.List;


/**
 * Any null field will be ignored when serializing to JSON
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract sealed class APIResponse<T> permits APIResponse.Success, APIResponse.Paginated, APIResponse.Error {
    private final boolean success;
    private final String message;
    private final long timestamp = Instant.now().toEpochMilli();

    protected APIResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Schema(name = Success.SCHEMA_NAME)
    @Getter
    public static final class Success<T> extends APIResponse<T>{
        public static final String SCHEMA_NAME="ResponseSuccess";
        private final T data;

        @Builder(access = AccessLevel.PRIVATE)
        private Success(T data, String message){
            super(true, message);
            this.data = data;
        }

    }

    @Schema(name = Paginated.SCHEMA_NAME)
    @Getter
    public static final class Paginated<T> extends APIResponse<T>{
        public static final String SCHEMA_NAME = "ResponseSuccessPaginated";
        private final List<T> data;
        private final PaginationMeta meta;

        @Builder(access = AccessLevel.PRIVATE)
        private Paginated(List<T> data, PaginationMeta meta, String message) {
            super(true, message);
            this.data = data;
            this.meta = meta;
        }
    }

    @Schema(name = Error.SCHEMA_NAME)
    @Getter
    public static final class Error extends APIResponse<Void> {
        public static final String SCHEMA_NAME = "ResponseError";
        private final String errorCode;
        private final Object errorDetails;

        @Builder(access = AccessLevel.PRIVATE)
        private Error(String errorCode, Object errorDetails, String message) {
            super(true, message);
            this.errorCode = errorCode;
            this.errorDetails = errorDetails;
        }
    }



    public static APIResponse.Success<Void> success() {
        return APIResponse.Success.<Void>builder().build();
    }
    public static <T> APIResponse.Success<T> success(T data){
        return Success.<T>builder()
                .data(data)
                .build();
    }

    public static <T> APIResponse.Success<T> success(T data, String message){
        return APIResponse.Success.<T>builder()
                .data(data)
                .message(message)
                .build();
    }

    public static <T> APIResponse<T> paginated(List<T> data, PaginationMeta meta){
        return APIResponse.Paginated.<T>builder()
                .data(data)
                .meta(meta)
                .build();
    }

    public static <T> APIResponse<T> paginated(List<T> data, PaginationMeta meta, String message){
        return APIResponse.Paginated.<T>builder()
                .data(data)
                .meta(meta)
                .message(message)
                .build();
    }

    public static APIResponse<Void> error(ErrorCode error){
        return APIResponse.Error.<Void>builder()
                .errorCode(error.getCode())
                .build();
    }

    public static APIResponse<Void> error(ErrorCode error, String message){
        return APIResponse.Error.<Void>builder()
                .errorCode(error.getCode())
                .message(message)
                .build();
    }

    public static APIResponse<Void> error(ErrorCode error, Object errorDetails){
        return APIResponse.Error.<Void>builder()
                .errorCode(error.getCode())
                .errorDetails(errorDetails)
                .build();
    }

}