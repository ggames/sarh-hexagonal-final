package com.fich.sarh.auth.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString                                                                                                               
@Builder
public class AuthResponse {
    Long id;
    String username;
    String message;
    String accessToken;
    @JsonIgnore
    String refreshToken;
    boolean mustChangePassword;
    Set<String> roles;
    boolean status;
}
