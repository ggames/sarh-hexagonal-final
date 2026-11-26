package com.fich.sarh.auth.domain.model;

import com.fich.sarh.auth.domain.model.RoleEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDTO {

    Long id;

    RoleEnum roleEnum;
}
