package vn.edu.ptithcm.mindcard.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedContentValidator.class)
public @interface AllowedContentType {
    String[] types();
    boolean allowEmpty() default false;

    String message() default "Invalid file content type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
