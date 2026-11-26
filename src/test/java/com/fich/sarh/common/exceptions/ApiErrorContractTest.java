package com.fich.sarh.common.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fich.sarh.auth.domain.model.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiErrorContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void passwordHashIsNeverSerializedButCanStillBeReceived() throws Exception {
        UserDTO user = UserDTO.builder().username("alice").password("$2a$10$hash").build();

        String json = objectMapper.writeValueAsString(user);
        assertThat(json).doesNotContain("password").doesNotContain("$2a$10$hash");

        UserDTO received = objectMapper.readValue("{\"username\":\"alice\",\"password\":\"Secret123\"}", UserDTO.class);
        assertThat(received.getPassword()).isEqualTo("Secret123");
    }

    @Test
    void malformedJsonIsClientErrorNotServerError() {
        WebRequest webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/position/create");

        var response = new GlobalExceptionHandler().handleMalformedRequest(
                new HttpMessageNotReadableException("bad json", (org.springframework.http.HttpInputMessage) null),
                webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).doesNotContain("bad json");
    }

    @Test
    void refreshRevocationUsesIndependentTransaction() throws Exception {
        var method = com.fich.sarh.auth.infrastructure.adapter.output.persistence.adapter.RefreshTokenPersistenceAdapter.class
                .getMethod("revokeFamily", String.class, java.time.Instant.class);
        var transactional = method.getAnnotation(org.springframework.transaction.annotation.Transactional.class);

        assertThat(transactional).isNotNull();
        assertThat(transactional.propagation()).isEqualTo(
                org.springframework.transaction.annotation.Propagation.REQUIRES_NEW);
    }
}
