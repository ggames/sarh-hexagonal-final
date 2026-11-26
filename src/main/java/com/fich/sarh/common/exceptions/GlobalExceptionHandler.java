package com.fich.sarh.common.exceptions;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fich.sarh.common.model.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse> handlerBusinessRuleViolationException(BusinessRuleViolationException exception,
                                                                        WebRequest webRequest){
        ApiResponse apiResponse = new ApiResponse(exception.getMessage(), webRequest.getDescription(false));
        return  new ResponseEntity<>(apiResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handlerResourceNotFoundException(ResourceNotFoundException ex, WebRequest webRequest){
        ApiResponse apiResponse = new ApiResponse(ex.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            ConstraintViolationException.class})
    public ResponseEntity<ApiResponse> handleMalformedRequest(Exception ex, WebRequest webRequest) {
        log.warn("Solicitud mal formada: {}", ex.getMessage());
        ApiResponse apiResponse = new ApiResponse(
                "La solicitud contiene datos mal formados o incompletos.",
                webRequest.getDescription(false));
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxUploadSizeExceeded(WebRequest webRequest) {
        ApiResponse apiResponse = new ApiResponse(
                "El archivo enviado supera el tamaño máximo permitido.",
                webRequest.getDescription(false));
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(apiResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest webRequest){
        ApiResponse apiResponse = new ApiResponse(
                "Conflicto de integridad de datos: el registro ya existe o viola una restricción del sistema.",
                webRequest.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(org.springframework.dao.OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse> handleOptimisticLockingFailure(
            org.springframework.dao.OptimisticLockingFailureException ex, WebRequest webRequest) {
        log.warn("Conflicto de concurrencia optimista: {}", ex.getMessage());
        ApiResponse apiResponse = new ApiResponse(
                "El recurso fue modificado simultáneamente por otra transacción. Por favor, recargue y vuelva a intentar.",
                webRequest.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest webRequest) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ApiResponse apiResponse = new ApiResponse(
                "Acceso denegado: no posee los permisos requeridos para esta acción.",
                webRequest.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex, WebRequest webRequest) {
        log.warn("Fallo de autenticación: {}", ex.getMessage());
        ApiResponse apiResponse = new ApiResponse(
                "Error de autenticación: credenciales inválidas o no autorizado.",
                webRequest.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ApiResponse> handleJwtVerificationException(JWTVerificationException ex, WebRequest webRequest) {
        log.warn("Token JWT rechazado: {}", ex.getMessage());
        ApiResponse apiResponse = new ApiResponse(
                "Token inválido o expirado.",
                webRequest.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex, WebRequest webRequest) {
        log.error("Excepción interna no controlada", ex);
        ApiResponse apiResponse = new ApiResponse(
                "Ha ocurrido un error interno en el servidor.",
                webRequest.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
