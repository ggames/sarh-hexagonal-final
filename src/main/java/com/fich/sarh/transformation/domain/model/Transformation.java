package com.fich.sarh.transformation.domain.model;

import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.validation.UniqueResolutionTransformation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transformation {

    Long id;

    LocalDate date;

    @UniqueResolutionTransformation
    @Pattern(
            regexp = "^[A-Z]{2}\\s\\d{4}/\\d{4}$",
            message = "Debe tener el formato XX 9999/9999"
    )
    String resolutionNumber;

    @NotNull(message = "El motivo de creación es obligatorio")
    @Size(min = 10, message = "La descripción del motivo tiene que tener al menos 10 caracteres")
    String reason;

    Set<String> attachedDocumentation;

    @Override
    public String toString() {
        return "Transformation{" +
                "id=" + id +
                ", date=" + date +
                ", resolutionNumber='" + resolutionNumber + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
