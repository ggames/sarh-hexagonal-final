package com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String email;
    private String username;
    private String profilePicturePath;
    private Set<RoleResponse> roles;
}
