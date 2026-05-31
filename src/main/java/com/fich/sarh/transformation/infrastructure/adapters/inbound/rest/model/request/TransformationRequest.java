package com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransformationRequest {

    LocalDate date;

    String resolutionNumber;

    String reason;

}
