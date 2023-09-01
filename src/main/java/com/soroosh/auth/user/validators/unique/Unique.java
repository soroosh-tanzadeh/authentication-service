package com.soroosh.auth.user.validators.unique;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValidator.class)
@Documented
public @interface Unique {
    String object();

    String field();

    String query() default "";

    String message() default "must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
