package com.fich.sarh.auth.infrastructure.adapter.input.rest.model.request;

import jakarta.validation.constraints.NotBlank;


public record LoginRequest(@NotBlank String username,
        @NotBlank String password) {


}
