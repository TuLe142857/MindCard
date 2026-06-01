---
trigger: always_on
---

# 1. API Naming Conventions
- **API Prefix (Class Level):** ALWAYS apply the `@RequestMapping("/api/<...>")` annotation at the Controller class 
level alongside `@RestController`. Example for UserController class:  
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
  // ...
}
```
- **Endpoints:** MUST use lowercase, plural nouns, and kebab-case (hyphens as separators).
    - *Example:* `/api/users/me/saved-decks`
    - *Warning:* Do not use verbs in the URL path unless it's a specific action that doesn't fit standard CRUD 
(e.g., `/api/documents/123/publish`).

# 2. Response Standardization (APIResponse)
All REST API endpoints MUST return data wrapped in the custom `APIResponse` generic class (`dto/response/common/APIResponse.java`).
- ALWAYS wrap the `APIResponse` inside Spring's `ResponseEntity`.
- **DO NOT use constructors** to instantiate `APIResponse`. Use the provided Factory Methods:
    - `APIResponse.Success<T>`: For successful operations returning normal data. Create via `APIResponse.success(data)`.
    - `APIResponse.Pagination<T>`: For paginated lists. Service layer must return a Spring `Page<T>`, 
and the controller must map it using `APIResponse.paginated(page)`. This automatically builds pagination metadata.
    - `APIResponse.Error`: Strictly for error responses. **Controllers MUST NOT return this directly.** Errors are 
automatically generated and returned to the client whenever an Exception is thrown and caught by the Global Exception Handler (`@RestControllerAdvice`).

# 3. DTO Definition Rules
- **Structure:** 
  - *Request:* Use Java `record` types combined with Lombok `@Builder` for immutability.
  - *Response:* Use `record` types (preferred) or `POJO` (Plain Old Java Object). In most cases, response DTOs should also be records.
- **Warning:** Because Java records are immutable and all fields are implicitly final, **DO NOT use Lombok `@Setter` or `@Data` on records.**
- **Documentation:** Use annotations from `springdoc-openapi` to describe DTOs and their fields.

## 3.1. Request Rules (Input)
- ALWAYS use custom DTO classes for request bodies or form parameters.
- **Validation:**
  - Use standard Spring Validation annotations (e.g., `@NotBlank`, `@Email`, `@Size`) on the fields inside DTOs 
as the primary validation method.
  - For complex or domain-specific validation logic (e.g., cross-field validation), create a Custom Validator. This 
strictly requires defining two components:
    1. A custom `@interface` annotation marked with `@Constraint(validatedBy = ...)`.
    2. A corresponding validator class implementing the `ConstraintValidator<YourCustomAnnotation, TargetType>` interface.
- **Controller Binding:** Use `@Valid` in combination with `@RequestBody`, `@RequestParam`, or `@ModelAttribute` to 
enforce validation before the request hits the Service layer.

## 3.2. Response Rules (Output)
- **No Entities:** NEVER return JPA Database Entities directly to the client. ALWAYS map them to Response DTOs.

# 4. API Documentation (Swagger/OpenAPI)
- **No Javadocs on Controllers:** Because Controller methods act as routing gateways, do not use standard JavaDocs for 
them. Use annotations from `springdoc-openapi` instead.
- **Grouping:** Use the `@Tag(name = "...")` annotation on the Controller class to name the API group.
- **Method Details:** Include `@Operation` on controller methods to clearly explain the API's purpose.
    - For simple, self-explanatory APIs, use `summary = "..."` for brevity.
    - Use `description = "..."` ONLY when the API logic is complex and requires detailed explanation.
- **Error Documentation:** Use the custom annotations `@ApiErrors` or `@ApiError` to define available error cases 
and automatically generate corresponding response examples.

*Example:*
```java
@RequestMapping("/api/health")
@Tag(name = "Health Check")
public class HealthCheckController {

    @GetMapping("")
    @Operation(summary = "Check server health status")
    @ApiErrors({@ApiError(value = ErrorCode.SERVER_ERROR, summary = "Server encounters an unexpected error")})
    public ResponseEntity<APIResponse<String>> healthCheck() {
        return ResponseEntity.ok(
                APIResponse.success("OK")
        );
    }
}
```