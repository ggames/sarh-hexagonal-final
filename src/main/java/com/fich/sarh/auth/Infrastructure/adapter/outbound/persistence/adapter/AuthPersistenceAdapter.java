package com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.adapter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fich.sarh.auth.Domain.ports.outbound.AuthSpiPort;
import com.fich.sarh.auth.Domain.ports.outbound.UserSpiPort;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.CustomUserDetails;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.jwt.JwtUtils;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.request.LoginRequest;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.response.AuthResponse;
import com.fich.sarh.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

@WebAdapter
@RequiredArgsConstructor
@Log4j2
public class AuthPersistenceAdapter implements AuthSpiPort {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserSpiPort userSpiPort;
   // private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("ENTRO A LOGUEARSE  " + request);


        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();



            boolean mustChangePassword = user.getMustChangePassword();
            String accessToken = jwtUtils.createToken(authentication, mustChangePassword);
            String refreshToken = jwtUtils.createRefreshToken(authentication);

            Set<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            return new AuthResponse(
                    user.getId(),
                    user.getUsername(),
                    mustChangePassword ? "PASSWORD_CHANGE_REQUIRED" : "LOGIN_OK",
                    accessToken,
                    refreshToken,
                    mustChangePassword,
                    authorities,
                    true
            );

        } catch (Exception e) {

            throw e;
        }

    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        DecodedJWT decoded = jwtUtils.validateRefreshToken(refreshToken);

        String username = decoded.getSubject();
        UserDetails userDetails =  userSpiPort.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = jwtUtils.createToken(authentication,false);

        Set<String> authorities =userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());


        return new AuthResponse(
                null,
                username,
                "ACCESS_TOKEN_REFRESHED",
                newAccessToken,
                refreshToken,
                false,
                authorities,
                true

        );
    }
}
