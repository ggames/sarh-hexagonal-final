package com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueResolutionTransformationValidator.class)
public @interface UniqueResolutionTransformation {
    String message() default  "La resolucón de transformación ingresada ya existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
