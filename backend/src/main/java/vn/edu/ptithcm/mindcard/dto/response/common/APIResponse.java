package vn.edu.ptithcm.mindcard.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import java.time.Instant;
import java.util.List;


/**
 * Any null field will be ignored when serializing to JSON
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Schema(description = "Standard API Response Wrapper")
public abstract sealed class APIResponse<T> permits APIResponse.Success, APIResponse.Paginated, APIResponse.Error {
    @Schema(description = "Indicates if the request was successful")
    private final boolean success;

    @Schema(description = "Response message", nullable = true)
    private final String message;

    @Schema(description = "Timestamp of the response in milliseconds")
    private final long timestamp = Instant.now().toEpochMilli();

    protected APIResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Getter
    @Schema(description = "Successful API Response with data")
    public static final class Success<T> extends APIResponse<T>{
        @Schema(description = "The payload data", nullable = true)
        private final T data;

        @Builder(access = AccessLevel.PRIVATE)
        private Success(T data, String message){
            super(true, message);
            this.data = data;
        }

    }

    @Getter
    @Schema(description = "Successful API Response with paginated data")
    public static final class Paginated<T> extends APIResponse<T>{
        @Schema(description = "List of data items for current page")
        private final List<T> data;

        @Schema(description = "Pagination metadata")
        private final PaginationMeta meta;

        @Builder(access = AccessLevel.PRIVATE)
        private Paginated(List<T> data, PaginationMeta meta, String message) {
            super(true, message);
            this.data = data;
            this.meta = meta;
        }
    }

    @Schema(name = Error.SCHEMA_NAME, description = "Error API Response")
    @Getter
    public static final class Error extends APIResponse<Void> {
        public static final String SCHEMA_NAME = "ResponseError";

        @Schema(description = "Specific application error code")
        private final String errorCode;

        @Schema(description = "Additional details about the error, like validation errors", nullable = true)
        private final Object errorDetails;

        @Builder(access = AccessLevel.PRIVATE)
        private Error(String errorCode, Object errorDetails, String message) {
            super(false, message);
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

    public static <T> APIResponse.Paginated<T> paginated(Page<T> page){
        List<T> data = page.getContent();
        PaginationMeta meta = PaginationMeta.fromPage(page);
        return Paginated.<T>builder()
                .data(data)
                .meta(meta)
                .build();
    }

    public static <T> APIResponse.Paginated<T> paginated(Page<T> page, String message){
        List<T> data = page.getContent();
        PaginationMeta meta = PaginationMeta.fromPage(page);
        return Paginated.<T>builder()
                .data(data)
                .meta(meta)
                .message(message)
                .build();
    }

    public static <T> APIResponse.Paginated<T> paginated(List<T> data, PaginationMeta meta){
        return APIResponse.Paginated.<T>builder()
                .data(data)
                .meta(meta)
                .build();
    }

    public static <T> APIResponse.Paginated<T> paginated(List<T> data, PaginationMeta meta, String message){
        return APIResponse.Paginated.<T>builder()
                .data(data)
                .meta(meta)
                .message(message)
                .build();
    }

    public static APIResponse.Error error(ErrorCode error){
        return APIResponse.Error.<Void>builder()
                .errorCode(error.getCode())
                .build();
    }

    public static APIResponse.Error error(ErrorCode error, String message){
        return APIResponse.Error.<Void>builder()
                .errorCode(error.getCode())
                .message(message)
                .build();
    }

    public static APIResponse.Error error(ErrorCode error, Object errorDetails){
        return APIResponse.Error.<Void>builder()
                .errorCode(error.getCode())
                .errorDetails(errorDetails)
                .build();
    }

}