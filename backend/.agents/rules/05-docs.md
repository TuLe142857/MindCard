---
trigger: always_on
---

# 1. JavaDoc Scope & Rules
To maintain high code quality without creating unnecessary maintenance overhead, JavaDocs are applied selectively based on complexity:

- **Mandatory JavaDocs:** JavaDocs are strictly required for:
  - All **Service layer methods** (`@Service` implementations) handling business logic.
  - Any helper components, utility classes, or methods containing **complex, custom, or non-trivial logic**.
- **Optional JavaDocs:** JavaDocs are optional and should be omitted unless they exhibit unique, complex, or non-standard behaviors for:
  - **DTOs** (Java records or POJOs).
  - **Repository interfaces** (Spring Data JPA interfaces extending `JpaRepository`).
  - Standard framework configuration classes.
- **Exception Rule (Controllers):** DO NOT write standard JavaDocs for Controller methods. Since they act purely as API gateways without business logic, use `springdoc-openapi` annotations instead (e.g., `@Operation`, `@ApiErrors`).
- **Required Components (When JavaDoc is applied):** A complete JavaDoc must include:
  - **Summary:** A concise description of what the method does.
  - `@param`: Parameters with short, meaningful descriptions.
  - `@return`: Description of the return value (if not void).
  - `@throws`: A list of exceptions that might be thrown.
  - `{@link}`: References to related classes or methods if necessary.

# 2. Strict AppException Declaration (CRITICAL)
- Even though `AppException` extends `RuntimeException` (making it an unchecked exception that Java does not force you to declare), it is the core mechanism for determining API error responses.
- Therefore, **ANY method that throws an `AppException` MUST explicitly declare `throws AppException`** in its method signature.
- **Detailed Error Codes:** Because `AppException` can carry various `ErrorCode` values depending on the scenario, the method's JavaDoc MUST explicitly list every possible `ErrorCode` it might throw and explain the exact conditions for each.

# 3. ErrorCode Traceability
- **Trace Back Requirement:** When a Controller invokes a Service method, the developer (and the AI Agent) MUST carefully trace back all potential `ErrorCode`s that the Service (and its underlying methods) might throw.
- **Sync with Swagger:** This tracing ensures that the Controller is fully aware of all possible error states, allowing accurate documentation using `@ApiErrors` in the API layer.

# 4. Implementation Template & Exception Tracing Example
When a Service depends on other Components/Helpers, the Service's JavaDoc MUST aggregate and document all `ErrorCode`s 
thrown by itself AND bubbled up from its dependencies.

```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.yourproject.exception.AppException;
import com.yourproject.exception.ErrorCode;

@Component
public class ActionValidator {
    /**
     * Validates if the user is allowed to perform the specific action.
     * * @param param The input parameter to check.
     * @throws AppException with the following {@link ErrorCode}s:
     * <ul>
     * <li>{@link ErrorCode#FORBIDDEN} - User does not have permission for this action.</li>
     * <li>{@link ErrorCode#ACTION_ALREADY_PERFORMED} - Action was already executed previously.</li>
     * </ul>
     */
    public void validateAction(String param) throws AppException {
        // Validation logic throwing FORBIDDEN or ACTION_ALREADY_PERFORMED
    }
}

@Service
@RequiredArgsConstructor
public class BusinessService {

    // ALWAYS use constructor injection via @RequiredArgsConstructor, DO NOT use @Autowired on fields.
    private final ActionValidator actionValidator;

    /**
     * Executes the core business logic after validating the parameters.
     *
     * @param param The input parameter for the business process.
     * @throws AppException if any business validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#RESOURCE_ALREADY_EXISTS} - Thrown locally if the resource is a duplicate.</li>
     * <li>{@link ErrorCode#FORBIDDEN} - Bubbled up from {@link ActionValidator#validateAction(String)}.</li>
     * <li>{@link ErrorCode#ACTION_ALREADY_PERFORMED} - Bubbled up from {@link ActionValidator#validateAction(String)}.</li>
     * </ul>
     * @see ActionValidator#validateAction(String)
     */
    public void executeProcess(String param) throws AppException {
        if (/* some condition */) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS);
        }
        
        // This method invocation can throw FORBIDDEN and ACTION_ALREADY_PERFORMED.
        // The developer/Agent MUST trace them and document them in the JavaDoc above.
        actionValidator.validateAction(param);
    }
}
```