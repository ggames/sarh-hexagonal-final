package com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.request;

import jakarta.validation.constraints.NotBlank;


public record LoginRequest(@NotBlank String username,
        @NotBlank String password) {


}
