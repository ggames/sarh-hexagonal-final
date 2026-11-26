package com.fich.sarh.it;

import com.fich.sarh.agent.application.usecases.AgentApiPortUseCases;
import com.fich.sarh.agent.domain.ports.inbound.AgentApiPort;
import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.agent.infrastructure.adapters.inbound.rest.controller.AgentController;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.mapper.AgentRestMapper;
import com.fich.sarh.auth.application.ports.entrypoint.api.AuthApiPort;
import com.fich.sarh.auth.application.ports.entrypoint.api.UserResetPasswordApiPort;
import com.fich.sarh.auth.infrastructure.adapter.input.rest.controller.AuthenticationController;
import com.fich.sarh.auth.application.model.AuthResponse;
import com.fich.sarh.common.exceptions.GlobalExceptionHandler;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.plantofpositions.domain.ports.inbound.PlantPositionApiPort;
import com.fich.sarh.point.application.usecases.PointUseCases;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import com.fich.sarh.point.infrastructure.adapter.input.rest.controller.PointController;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.mapper.PointRestMapper;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import com.fich.sarh.testsupport.SpringValidatorSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.Cookie;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de la capa REST: controlador + Bean Validation +
 * {@link GlobalExceptionHandler}, con los puertos mockeados (sin contexto
 * Spring completo ni base de datos).
 */
@DisplayName("D. Otras reglas — integración de la API REST")
class RestApiRulesIntegrationTest {

    private static final String MENSAJE_PUNTO_ASIGNADO =
            "No se puede eliminar el punto/categoría porque está asignado a uno o más cargos. en el sistema.";
    private static final String MENSAJE_DOCUMENTO_DUPLICADO =
            "Ya existe un agente con el documento 87654321 en el sistema.";

    private MockMvc mockMvcDePuntos(PointApiPort pointApiPort) {
        return MockMvcBuilders
                .standaloneSetup(new PointController(pointApiPort, PointRestMapper.INSTANCE))
                .setValidator(SpringValidatorSupport.validatorWithBeans())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private MockMvc mockMvcDeAgentes(AgentApiPort agentApiPort) {
        return MockMvcBuilders
                .standaloneSetup(new AgentController(agentApiPort, mock(AgentRestMapper.class)))
                .setValidator(SpringValidatorSupport.validatorWithBeans())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("50. Eliminar un punto asignado a cargos responde 422 con el mensaje de la regla de negocio")
    void eliminarPuntoAsignadoResponde422ConElMensaje() throws Exception {
        PointSpiPort pointSpiPort = mock(PointSpiPort.class);
        PositionApiPort positionApiPort = mock(PositionApiPort.class);
        when(pointSpiPort.findPointById(999L))
                .thenReturn(Optional.of(com.fich.sarh.point.domain.model.Point.builder().id(999L).build()));
        when(positionApiPort.existsByPointId(999L)).thenReturn(true);
        PointApiPort pointApiPort = new PointUseCases(pointSpiPort, positionApiPort);

        mockMvcDePuntos(pointApiPort)
                .perform(delete("/point/999"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(MENSAJE_PUNTO_ASIGNADO));
    }

    @Test
    @DisplayName("51. Crear un agente con documento duplicado responde 422 con el mensaje de unicidad")
    void agenteConDocumentoDuplicadoResponde422ConElMensajeDeUnicidad() throws Exception {
        AgentSpiPort agentSpiPort = mock(AgentSpiPort.class);
        when(agentSpiPort.existByDocumentAgent("87654321")).thenReturn(true);
        AgentApiPort agentApiPort = new AgentApiPortUseCases(
                agentSpiPort,
                mock(PlantPositionApiPort.class),
                mock(OrganizationalunitApiPort.class));
        String cuerpo = """
                {
                  "firstname": "Maria",
                  "lastname": "Lopez",
                  "documenttype": "DNI",
                  "document": "87654321",
                  "birthdate": "1990-05-20",
                  "file": "LEG-01",
                  "address": "Calle Falsa 123",
                  "email": "maria@correo.com"
                }
                """;

        mockMvcDeAgentes(agentApiPort)
                .perform(post("/agent/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(MENSAJE_DOCUMENTO_DUPLICADO));
    }

    @Test
    @DisplayName("52. Crear un agente con campos inválidos responde 400 con el mapa campo -> mensaje")
    void agenteConCamposInvalidosResponde400ConLosMensajesDeCampo() throws Exception {
        String cuerpo = """
                {
                  "firstname": "A",
                  "lastname": "Lopez",
                  "documenttype": "DNI",
                  "document": "",
                  "birthdate": "1990-05-20",
                  "file": "LEG-01",
                  "address": "Calle Falsa 123",
                  "email": "no-es-un-correo"
                }
                """;

        mockMvcDeAgentes(mock(AgentApiPort.class))
                .perform(post("/agent/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstname").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.document").exists());
    }

    @Test
    @DisplayName("53. Login sin contraseña responde 400 con el error del campo password")
    void loginSinPasswordResponde400ConElErrorDelCampo() throws Exception {
        MockMvc mvc = MockMvcBuilders
                .standaloneSetup(new AuthenticationController(
                        mock(AuthApiPort.class),
                        mock(UserResetPasswordApiPort.class)))
                .setValidator(SpringValidatorSupport.validatorWithBeans())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc.perform(post("/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").exists());
    }

    private MockMvc mockMvcDeAuth(AuthApiPort authApiPort) {
        return MockMvcBuilders
                .standaloneSetup(new AuthenticationController(
                        authApiPort,
                        mock(UserResetPasswordApiPort.class)))
                .setValidator(SpringValidatorSupport.validatorWithBeans())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("54. El login entrega el refresh en cookie HttpOnly y no en el cuerpo")
    void loginEntregaRefreshEnCookieHttpOnly() throws Exception {
        AuthApiPort authApiPort = mock(AuthApiPort.class);
        when(authApiPort.login(any())).thenReturn(new AuthResponse(
                1L, "admin", "LOGIN_OK", "access-jwt", "refresh-jwt",
                false, Set.of("ROLE_ADMIN"), true));

        mockMvcDeAuth(authApiPort)
                .perform(post("/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refreshToken=refresh-jwt")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=/auth")))
                .andExpect(jsonPath("$.accessToken").value("access-jwt"))
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
    }

    @Test
    @DisplayName("55. El refresh lee el token de la cookie")
    void refreshLeeTokenDeLaCookie() throws Exception {
        AuthApiPort authApiPort = mock(AuthApiPort.class);
        when(authApiPort.refreshToken("refresh-jwt")).thenReturn(new AuthResponse(
                null, "admin", "ACCESS_TOKEN_REFRESHED", "new-access-jwt", "refresh-jwt",
                false, Set.of("ROLE_ADMIN"), true));

        mockMvcDeAuth(authApiPort)
                .perform(post("/auth/refresh").cookie(new Cookie("refreshToken", "refresh-jwt")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-jwt"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refreshToken=refresh-jwt")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=/auth")))
                .andExpect(jsonPath("$.refreshToken").doesNotExist());

        verify(authApiPort).refreshToken("refresh-jwt");
    }

    @Test
    @DisplayName("56. El refresh sin cookie responde 401")
    void refreshSinCookieResponde401() throws Exception {
        mockMvcDeAuth(mock(AuthApiPort.class))
                .perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("57. El logout revoca la familia y expira la cookie")
    void logoutRevocaFamiliaYExpiraCookie() throws Exception {
        AuthApiPort authApiPort = mock(AuthApiPort.class);

        mockMvcDeAuth(authApiPort)
                .perform(post("/auth/logout").cookie(new Cookie("refreshToken", "refresh-jwt")))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        verify(authApiPort).logout("refresh-jwt");
    }
}
