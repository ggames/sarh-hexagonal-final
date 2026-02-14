package com.fich.sarh.auth.Infrastructure.adapter.configuration.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.datasource.DatabaseContextHolder;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.datasource.DatabaseType;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.CustomUserDetails;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.SecurityConfig;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtUtils jwtUtils;

    // 🌍 Endpoints públicos (NO JWT)
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/auth/",
            "/uploads/",
            "/actuator/",
            "/swagger-ui",
            "/v3/api-docs"
    );

    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/auth/change",
            "/auth/logout",
            "/auth/refresh"
    );

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.startsWith("/uploads/");

      /*  return path.startsWith("/auth/")
                || path.startsWith("/uploads/");*/

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path =request.getRequestURI();

        try {

            if(path.startsWith("/auth")){
                DatabaseContextHolder.setDatabaseType(DatabaseType.AUTH);
                filterChain.doFilter(request, response);
                return;
            }
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if( authHeader != null && authHeader.startsWith("Bearer ")){
                String token = authHeader.substring(7);

                if(jwtUtils.isTokenValid(token)){

                    DecodedJWT decodedJWT = jwtUtils.validateToken(token);
                    DatabaseType dbType = jwtUtils.extractDatabase(decodedJWT);

                    DatabaseContextHolder.setDatabaseType(dbType);

                    CustomUserDetails userDetails = jwtUtils.buildUserDetails(decodedJWT);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    DatabaseContextHolder.setDatabaseType(DatabaseType.PROD);
                }
            } else {
                DatabaseContextHolder.setDatabaseType(DatabaseType.PROD);
            }

            filterChain.doFilter(request, response);

        } finally {
            DatabaseContextHolder.clear();
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isAllowPath(HttpServletRequest request) {

        String path = request.getRequestURI();

        logger.warn("EL PATH es :", path);

        return ALLOWED_PATHS.stream().anyMatch(path::contains);
    }


}
/*
   protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {


        String path = request.getRequestURI();

        if (path.startsWith("/auth/")) {
            DatabaseContextHolder.setDatabaseType(DatabaseType.AUTH);
            logger.info(">>> USING AUTH DATABASE FOR {}", path);
        } else {
            DatabaseContextHolder.setDatabaseType(DatabaseType.PROD);
            logger.info(">>> USING PROD DATABASE FOR {}", path);
        }

        try {
            logger.error(">>> JWT FILTER EJECUTADO PARA {}", request.getRequestURI());

            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            if (!jwtUtils.isTokenValid(token)) {
                logger.warn("Token no válido o expirado para URI: {}", request.getRequestURI());
                // No se establece la autenticación, el SecurityConfig lo manejará.
                filterChain.doFilter(request, response);
                return;
            }

            try {

                // 3. Decodificar el JWT y construir CustomUserDetails
                DecodedJWT decodedJWT = jwtUtils.validateToken(token);

                // 4. Construir CustomUserDetails y el objeto Authentication
                CustomUserDetails userDetails = jwtUtils.buildUserDetails(decodedJWT);

                // El token de acceso solo debería llegar a rutas que requieren autenticación,
                // pero si la validación JWT es exitosa, se considera auténtico.
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());

                // 5. Establecer la autenticación en el SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Usuario autenticado: {}", userDetails.getUsername());

            } catch (JWTVerificationException e) {
                // Esto es redundante si se usa isTokenValid, pero se mantiene para robustez.
                logger.warn("Error al validar el token (aunque isTokenValid pasó): {}", e.getMessage());
                // No se establece la autenticación, se permite seguir a la cadena.
            }


        } finally {

            filterChain.doFilter(request, response);

            // limpiar contexto (MUY IMPORTANTE)
            DatabaseContextHolder.clear();
        }



    }
 */