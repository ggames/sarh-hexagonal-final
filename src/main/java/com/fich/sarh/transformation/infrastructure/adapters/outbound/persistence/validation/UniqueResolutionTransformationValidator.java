package com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.validation;

import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueResolutionTransformationValidator implements
        ConstraintValidator<UniqueResolutionTransformation, String> {

   private final TransformationApiPort transformationApiPort;

    @Override
    public boolean isValid(String resolutionNumber, ConstraintValidatorContext constraintValidatorContext) {

        System.out.println("Validando resolución: " + resolutionNumber);

        boolean exists =
                transformationApiPort.existByResolutionNumber(resolutionNumber);

        System.out.println("Existe: " + exists);

        return !exists;
    }
}
