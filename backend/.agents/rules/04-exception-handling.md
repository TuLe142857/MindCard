---
trigger: always_on
---

# 1. Global Exception Handling Strategy
- The project uses a custom `AppException`(extending `RuntimeException` to avoid boilerplate `try-catch` blocks)
combined with an `ErrorCode` enum to standardize error responses.
- All business logic exceptions MUST be thrown using `AppException`. These are caught by the `GlobalExceptionHandler`
(`@RestControllerAdvice`), which automatically builds the standard `APIResponse.Error` using the details from the `ErrorCode`.

# 2. ErrorCode Definition Rules
- **Structure:** The `ErrorCode` enum contains a `code` (String, usually the enum name) and an `httpStatusCode` (int).
- **Adding New Codes:** When a new error type is needed, add a new enum value to `ErrorCode.java` with the appropriate 
HTTP Status Code.
- **Constructor Usage:** By default, use the 1-parameter constructor `ErrorCode(int httpStatusCode)`. This automatically
assigns the Enum's exact name as the string `code`.

# 3. Naming Convention & Reusability (CRITICAL)
- **Prioritize Reusability:** Define highly reusable, generic ErrorCodes rather than hyper-specific ones.
  - *Example:* Use `RESOURCE_ALREADY_EXISTS` instead of `EMAIL_ALREADY_EXISTS`.
- **Dynamic Messages:** If you need to specify which resource failed, pass a custom message when throwing the exception,
while reusing the generic ErrorCode.
  - *Example:* `throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Email already exists");`
- **Specific Codes:** Only create highly specific ErrorCodes for critical or specialized domain logic where the client
needs a distinct code to trigger a specific UI behavior.