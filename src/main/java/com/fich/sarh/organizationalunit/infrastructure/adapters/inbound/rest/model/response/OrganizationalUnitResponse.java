package com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.model.response;

import com.fich.sarh.agent.domain.model.Agent;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationalUnitResponse {

    Long id;

    String nameUnit;

    Agent director;

    Agent viceDirector;

    //List<OrganizationalSubUnit> subunitList;

}
