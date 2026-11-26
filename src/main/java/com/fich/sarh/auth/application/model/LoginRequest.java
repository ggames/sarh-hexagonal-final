package com.fich.sarh.auth.application.model;

import jakarta.validation.constraints.NotBlank;


public record LoginRequest(@NotBlank String username,
        @NotBlank String password) {


}
