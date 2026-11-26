package com.fich.sarh.security;

import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.entity.AgentEntity;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.repository.AgentRepository;
import com.fich.sarh.auth.infrastructure.adapter.configuration.security.CustomUserDetails;
import com.fich.sarh.auth.infrastructure.adapter.configuration.security.jwt.JwtUtils;
import com.fich.sarh.common.DocumentType;
import com.fich.sarh.organizationalunit.infrastructure.adapters.outbound.persistence.entity.OrganizationalUnitEntity;
import com.fich.sarh.organizationalunit.infrastructure.adapters.outbound.persistence.repository.OrganizationalUnitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private OrganizationalUnitRepository organizationalUnitRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void protectedEndpointRejectsAnonymousRequest() throws Exception {
        mockMvc.perform(get("/position/allposition")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void corsPreflightFromAllowedOriginIsAccepted() throws Exception {
        mockMvc.perform(options("/position/allposition")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void refreshEndpointRequiresCsrfToken() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedRequestCanMapLazyRelationsWithoutOpenInView() throws Exception {
        new TransactionTemplate(transactionManager).execute(status -> {
            AgentEntity director = agentRepository.save(AgentEntity.builder()
                    .firstname("Ana")
                    .lastname("Pérez")
                    .documenttype(DocumentType.DNI)
                    .document("33333333C")
                    .build());
            organizationalUnitRepository.save(OrganizationalUnitEntity.builder()
                    .nameUnit("Dirección")
                    .director(director)
                    .build());
            return null;
        });

        CustomUserDetails user = new CustomUserDetails(
                1L, "admin", null,
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                true, true, true, true, false);
        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        String token = jwtUtils.createToken(authentication, false);

        mockMvc.perform(get("/organizational/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].director.firstname").value("Ana"));
    }

    private String tokenWithRole(String username, String role) {
        CustomUserDetails user = new CustomUserDetails(
                1L, username, null,
                AuthorityUtils.createAuthorityList(role),
                true, true, true, true, false);
        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        return jwtUtils.createToken(authentication, false);
    }

    @Test
    void adminEndpointDeniesRoleUserWithForbidden() throws Exception {
        String token = tokenWithRole("regularUser", "ROLE_USER");

        mockMvc.perform(post("/api/admin/user/999/reset-password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointAllowsRoleAdmin() throws Exception {
        String token = tokenWithRole("adminUser", "ROLE_ADMIN");

        mockMvc.perform(post("/api/admin/user/999/reset-password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void onlyConsultRoleCanReadUsersButCannotDeleteUsers() throws Exception {
        String token = tokenWithRole("consultUser", "ROLE_ONLY_CONSULT");

        mockMvc.perform(get("/user/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/user/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
