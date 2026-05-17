package vn.edu.ptithcm.mindcard.annotation;


import vn.edu.ptithcm.mindcard.config.OpenApiConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @see OpenApiConfig#apiErrorResponseCustomizer()
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrors {
    ApiError[] value() default {};
}
