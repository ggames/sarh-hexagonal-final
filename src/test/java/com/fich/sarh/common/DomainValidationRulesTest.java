package com.fich.sarh.common;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.auth.application.ports.output.persistence.UserRetrieveSpiPort;
import com.fich.sarh.auth.domain.model.UserDTO;
import com.fich.sarh.auth.infrastructure.adapter.validation.UniqueUsernameValidator;
import com.fich.sarh.testsupport.SpringValidatorSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Reglas de negocio declaradas en los modelos de dominio (Bean Validation):
 * campos obligatorios del agente, formato de correo y política de contraseñas.
 *
 * <p>La unicidad de username hoy vive solo en {@link UniqueUsernameValidator}
 * ({@code @UniqueUsername} está deshabilitado en {@code UserDTO}), por lo que
 * se prueba directamente contra su puerto.</p>
 */
@DisplayName("D. Otras reglas — validación de dominio (Bean Validation)")
class DomainValidationRulesTest {

    private static final String MENSAJE_PASSWORD =
            "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número";

    private Agent agenteValido() {
        return Agent.builder()
                .firstname("Maria")
                .lastname("Lopez")
                .documenttype(DocumentType.DNI)
                .document("12345678")
                .birthdate(LocalDate.of(1990, 5, 20))
                .file("LEG-01")
                .address("Calle Falsa 123")
                .email("maria@correo.com")
                .build();
    }

    private Set<ConstraintViolation<Object>> validar(Validator validator, Object objeto) {
        return validator.validate(objeto);
    }

    @Test
    @DisplayName("46. Un nombre de un solo carácter viola la longitud mínima del agente")
    void nombreDeUnCaracterGeneraViolacion() {
        Validator validator = SpringValidatorSupport.validatorWithBeans();
        Agent agente = agenteValido();
        agente.setFirstname("A");

        Set<ConstraintViolation<Object>> violaciones = validar(validator, agente);

        assertThat(violaciones)
                .anySatisfy(violacion -> {
                    assertThat(violacion.getPropertyPath().toString()).isEqualTo("firstname");
                    assertThat(violacion.getMessage()).isEqualTo("El nombre debe tener al menos 2 caracteres");
                });
    }

    @Test
    @DisplayName("47. Un correo electrónico con formato inválido viola la regla del agente")
    void correoElectronicoInvalidoGeneraViolacion() {
        Validator validator = SpringValidatorSupport.validatorWithBeans();
        Agent agente = agenteValido();
        agente.setEmail("no-es-un-correo");

        Set<ConstraintViolation<Object>> violaciones = validar(validator, agente);

        assertThat(violaciones)
                .anySatisfy(violacion -> {
                    assertThat(violacion.getPropertyPath().toString()).isEqualTo("email");
                    assertThat(violacion.getMessage()).isEqualTo("El correo electronico es invalido");
                });
    }

    @Test
    @DisplayName("48. Una contraseña sin mayúscula ni número viola la política de contraseñas")
    void passwordDebilGeneraViolacion() {
        Validator validator = SpringValidatorSupport.validatorWithBeans();
        UserDTO usuario = UserDTO.builder()
                .email("user@correo.com")
                .username("usuario1")
                .password("solo_minusus")
                .build();

        Set<ConstraintViolation<Object>> violaciones = validar(validator, usuario);

        assertThat(violaciones)
                .anySatisfy(violacion -> {
                    assertThat(violacion.getPropertyPath().toString()).isEqualTo("password");
                    assertThat(violacion.getMessage()).isEqualTo(MENSAJE_PASSWORD);
                });
    }

    @Test
    @DisplayName("49. El validador de username rechaza un nombre de usuario ya registrado")
    void usernameDuplicadoEsRechazadoPorElValidador() {
        UserRetrieveSpiPort port = mock(UserRetrieveSpiPort.class);
        when(port.existsUsername("duplicado")).thenReturn(true);
        UniqueUsernameValidator validator = new UniqueUsernameValidator(port);
        ConstraintValidatorContext contexto = mock(ConstraintValidatorContext.class);

        assertThat(validator.isValid("duplicado", contexto)).isFalse();
        assertThat(validator.isValid("libre123", contexto))
                .withFailMessage("con puerto sin registrar el username debería ser válido")
                .isTrue();
    }
}
