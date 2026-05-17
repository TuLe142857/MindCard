package vn.edu.ptithcm.mindcard.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import vn.edu.ptithcm.mindcard.annotation.ApiError;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;

import java.util.*;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI documentHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MindCard API")
                        .version("1.0.0")
                        .description("Hello World :)))")
                );
    }

    @Bean
    @Order(1)
    public OperationCustomizer apiErrorResponseCustomizer(){
        return (operation, handlerMethod) -> {
            ApiError[] declaredErrors = handlerMethod.getMethod().getAnnotationsByType(ApiError.class);
            if (declaredErrors.length == 0){
                return operation;
            }

            Map<Integer, List<ApiError>> groupErrors = groupErrorByStatus(declaredErrors);

            ApiResponses responsesMap = Optional
                    .ofNullable(operation.getResponses())
                    .orElseGet(()-> {
                                ApiResponses res = new ApiResponses();
                                operation.setResponses(res);
                                return res;
                            }
                    );

            for (var entry: groupErrors.entrySet()){
                String httpStatusStr = String.valueOf(entry.getKey());
                List<ApiError> errors = entry.getValue();

                ApiResponse response = responsesMap.computeIfAbsent(httpStatusStr, key -> createErrorResponse()) ;

                MediaType mediaType = response.getContent().get("application/json");

                for (var error : errors){
                    Example example = buildExampleErrorResponse(error.value(), error.summary(), error.description());
                    mediaType.addExamples(error.value().getCode(), example);
                }
            }
            return operation;
        };
    }

    @Bean
    @Order(2)
    public OperationCustomizer addSystemErrorResponse(){
        return (operation, handlerMethod) -> {
            ApiResponses responsesMap = Optional
                    .ofNullable(operation.getResponses())
                    .orElseGet(()-> {
                        ApiResponses res = new ApiResponses();
                        operation.setResponses(res);
                        return res;
                    }
            );


            ApiResponse response = responsesMap.computeIfAbsent(
                    String.valueOf(ErrorCode.SERVER_ERROR.getHttpStatusCode()),
                    k -> createErrorResponse()
            );

            MediaType mediaType = response.getContent().get("application/json");
            Map<String, Example> examples = mediaType.getExamples();

            if (examples != null && examples.containsKey(ErrorCode.SERVER_ERROR.getCode())){
                return operation;
            }else{
                Example example = buildExampleErrorResponse(
                        ErrorCode.SERVER_ERROR,
                        null,
                        "This happen when server get some unexpected error"
                );
                mediaType.addExamples(ErrorCode.SERVER_ERROR.getCode(), example);
            }
            return operation;
        } ;
    }


    private static Map<Integer, List<ApiError>> groupErrorByStatus(ApiError[] errors){
        Map<Integer, List<ApiError>> groupErrors = new HashMap<>();
        for (var error: errors){
            ErrorCode errorCode = error.value();
            groupErrors.computeIfAbsent(errorCode.getHttpStatusCode(), key -> new ArrayList<ApiError>()).add(error);
        }
        return groupErrors;
    }

    private static ApiResponse createErrorResponse(){
        MediaType mediaType = new MediaType();
        Schema schema = new Schema<>().$ref(Components.COMPONENTS_SCHEMAS_REF + APIResponse.Error.SCHEMA_NAME);
        mediaType.setSchema(schema);
        Content content = new Content().addMediaType("application/json", mediaType);
        return new ApiResponse().content(content);
    }

    private static Example buildExampleErrorResponse(ErrorCode errorCode, String summary, String description){
        Example example = new Example();

        example.setSummary((summary != null && !summary.isBlank()) ? summary  : errorCode.getCode());
        example.setDescription(description != null ? description : "");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("success", false);
        errorBody.put("errorCode", errorCode.getCode());
        errorBody.put("message", "string");
        errorBody.put("timestamp", 0);
        errorBody.put("errorDetails", "any|null");

        example.setValue(errorBody);
        return example;
    }


}