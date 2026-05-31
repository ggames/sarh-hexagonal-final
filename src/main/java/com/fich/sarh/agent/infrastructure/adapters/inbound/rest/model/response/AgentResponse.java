package com.fich.sarh.agent.infrastructure.adapters.inbound.rest.model.response;

import com.fich.sarh.common.DocumentType;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgentResponse {

    private Long id;

    private String firstname;

    private String lastname;

    private DocumentType documenttype;

    private String document;

    private LocalDate birthdate;

    private LocalDate leavingdate;

    private boolean deceased;

    private String file;

    private String email;

    private String phone;

    private String address;

}
