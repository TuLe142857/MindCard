package vn.edu.ptithcm.mindcard.annotation;

import vn.edu.ptithcm.mindcard.exception.ErrorCode;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApiErrors.class)
public @interface ApiError {
    ErrorCode value();
    String description() default "";
    String summary() default "";
}
