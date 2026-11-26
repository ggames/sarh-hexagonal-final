package com.fich.sarh.auth.application.model;

import com.fich.sarh.auth.domain.model.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    Long id;
    @Enumerated(EnumType.STRING)
    RoleEnum roleEnum;
}
