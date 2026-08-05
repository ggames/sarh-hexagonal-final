package com.fich.sarh.auth.Infrastructure.adapter.configuration.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.CustomUserDetails;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            if (jwtUtils.isTokenValid(token)) {

                DecodedJWT jwt =
                        jwtUtils.validateToken(token);

                CustomUserDetails userDetails =
                        jwtUtils.buildUserDetails(jwt);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(auth);

                logger.info(
                        "Usuario autenticado: {}",
                        userDetails.getUsername()
                );
            }
        }

        filterChain.doFilter(request, response);
    }
    /*protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();


        if (path.startsWith("/auth")) {

            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtils.isTokenValid(token)) {

                DecodedJWT decodedJWT = jwtUtils.validateToken(token);

                CustomUserDetails userDetails = jwtUtils.buildUserDetails(decodedJWT);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);


        }


    } */
}
