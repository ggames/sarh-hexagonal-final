package com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Documented
public @interface UniqueUsername {

    String message() default  "El nombre de usuario ya está registrado";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
