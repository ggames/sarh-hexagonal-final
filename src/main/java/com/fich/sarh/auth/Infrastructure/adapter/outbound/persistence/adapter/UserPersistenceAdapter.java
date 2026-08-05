package com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.adapter;

import com.fich.sarh.auth.Domain.model.UserDTO;
import com.fich.sarh.auth.Domain.ports.inbound.RoleApiPort;
import com.fich.sarh.auth.Domain.ports.outbound.UserSpiPort;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.CustomUserDetails;
import com.fich.sarh.auth.Infrastructure.adapter.configuration.security.jwt.JwtUtils;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.mapper.UserRestMapper;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.entities.UserEntity;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.mapper.UserMapper;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.repository.UserRepository;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.service.IUserService;
import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebAdapter

@Log4j2
public class UserPersistenceAdapter implements UserSpiPort {


    public UserPersistenceAdapter(@Lazy PasswordEncoder passwordEncoder,
                                  UserRepository userRepository,
                                  RoleApiPort roleApiPort,
                                  IUserService userService,
                                  UserMapper mapper,
                                  UserRestMapper restMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleApiPort = roleApiPort;
        this.userService = userService;
        this.mapper = mapper;
        this.restMapper = restMapper;
    }

    private final PasswordEncoder passwordEncoder;

    private JwtUtils jwtUtils;
    private final UserRepository userRepository;

    private final RoleApiPort roleApiPort;
    private final IUserService userService;

    private final UserMapper mapper;
    private final UserRestMapper restMapper;

  /*  @Override
    public AuthResponse createUser(UserRequest request) {
        Set<String> roles = request.getRoles().stream()
                .map(r -> r.getRoleEnum().name())
                .collect(Collectors.toSet());

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roleRepository.findRoleEntitiesByRoleEnumIn(roles))
                .build();

        userRepository.save(user);

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                "USER_CREATED",
                null,
                null,
                true,
                roles,
                true
        );
    }*/


    @Override
    public UserDTO saveUser(UserDTO createUser) {

        var userEntity = mapper.toUserEntity(createUser);

        return mapper.toUserDTO(userRepository.save(userEntity));
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(mapper::toUserDTO).toList();
    }

    @Override
    public List<UserDTO> findUserByUsernameAndEmail(String query) {

        return userRepository.findUserByUsernameAndEmail(query)
                .stream().map(mapper::toUserDTO).toList();
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(mapper::toUserDTO);

    }

    @Override
    public Optional<UserDTO> findUserById(Long userId) {
        return userRepository.findById(userId).map(mapper::toUserDTO);
    }

    @Override
    public byte[] getPhotoByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario"));
        String photoProfile = user.getProfilePicturePath();
        String basePath = new File(".").getPath();

        try {
            Path path = Paths.get(basePath, photoProfile).normalize();
            log.info("ESTA ES LA FOTO DE PERFIL " + basePath + "   " + path);
            return Files.readAllBytes(Paths.get(path.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la foto " + e.getMessage());
        }


    }

    @Override
    public boolean existsUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id) {
        return userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id);
    }

    @Override
    public void sendEmailResetPassword(String newPassword, String email) {
        userService.sendEmailResetPassword(newPassword, email);
    }

    @Override
    public Optional<String> resetPasswordByAdmin(Long userId) {
        return userRepository.findById(userId).map(user -> {
            String tempPassword = UUID.randomUUID().toString().substring(0, 10);

            user.setPassword(passwordEncoder.encode(tempPassword));

            user.setMustChangePassword(true);

            userRepository.save(user);

            userService.sendEmailResetPassword(tempPassword, user.getEmail());

            return tempPassword;

        });
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        return userRepository.findById(userId).map(user -> {
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return false;
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setMustChangePassword(false);

            userRepository.save(user);

            return true;

        }).orElse(false);
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO dto) {
        return null;
    }

    @Override
    public String uploadProfilePicture(MultipartFile file) {
        return userService.uploadProfilePicture(file);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        UserEntity userEntity = userRepository.findUserEntityByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        log.info("BUSCANDO USUARIO {}", userEntity);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userEntity.getRoles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleEnum().name())));

        userEntity.getRoles().stream().flatMap(role -> role.getPermissionSet().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        return new CustomUserDetails(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword(),
                authorities,
                userEntity.isEnabled(),
                userEntity.isAccountNonLocked(),
                userEntity.isAccountNonExpired(),
                userEntity.isCredentialNonExpired(),
                userEntity.isMustChangePassword()
        );
    }
}
