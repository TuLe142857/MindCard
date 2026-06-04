package vn.edu.ptithcm.mindcard.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortValidator.class)
public @interface ValidSort {

    String message() default "Invalid sort parameters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedFields() default {};
}
