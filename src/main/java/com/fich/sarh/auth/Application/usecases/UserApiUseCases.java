package com.fich.sarh.auth.Application.usecases;

import com.fich.sarh.auth.Domain.model.UserDTO;
import com.fich.sarh.auth.Domain.ports.inbound.RoleApiPort;
import com.fich.sarh.auth.Domain.ports.inbound.UserApiPort;
import com.fich.sarh.auth.Domain.ports.outbound.UserSpiPort;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.response.AuthResponse;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.entities.RoleEntity;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.entities.UserEntity;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.mapper.RoleMapper;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.mapper.UserMapper;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.service.IUserService;
import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
@Log4j2
public class UserApiUseCases  implements UserApiPort {

    private final IUserService userService;
    private final RoleApiPort roleApiPort;
    private final PasswordEncoder passwordEncoder;
    private final UserSpiPort userSpiPort;
   // private final UserDetailsService userDetailsService;

    private final UserMapper mapper;
    private final RoleMapper roleMapper;


    @Override
    public AuthResponse createUser(UserDTO createUser, MultipartFile file) {
        Set<String> roles = createUser.getRoles().stream()
                .map(role ->
                 role.getRoleEnum().name())
                .collect(Collectors.toSet());



        String filename = userService.uploadProfilePicture(file);

        Set<RoleEntity> rolesEntities = roleApiPort.findRoleEntitiesByRoleEnumIn(roles).stream()
                .map(roleMapper::toEntity).collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .username(createUser.getUsername())
                .password(passwordEncoder.encode(createUser.getPassword()))
                .email(createUser.getEmail())
                .profilePicturePath(filename)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialNonExpired(true)
                .roles(rolesEntities)
                .build();

       var userDto = mapper.toUserDTO(userEntity);
        var  savedUser = userSpiPort.saveUser(userDto);
         return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                "USER_CREATED",
                null,
                null,
                true,
                roles,
                true
        );
    }

   /* @Override
    public AuthResponse saveUser(UserDTO request) {
        return  // userSpiPort.createUser(request);
    }*/

    @Override
    public List<UserDTO> findAllUsers() {
        return userSpiPort.findAllUsers();
    }

    @Override
    public List<UserDTO> findUserByUsernameAndEmail(String query) {
        return userSpiPort.findUserByUsernameAndEmail(query);
    }

    @Override
    public UserDTO findByUsername(String username) {
        return userSpiPort.findByUsername(username)
                .orElseThrow( ()-> new ResourceNotFoundException("Usuario"));
    }

    @Override
    public UserDTO findUserById(Long userId) {
        return userSpiPort.findUserById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario"));
    }

    @Override
    public byte[] getPhotoByUsername(String username) {
        return userSpiPort.getPhotoByUsername(username);
    }

    @Override
    public boolean existsUsername(String username) {
        return userSpiPort.existsUsername(username);
    }

    @Override
    public void sendEmailResetPassword(String newPassword, String email) {
          userSpiPort.sendEmailResetPassword(newPassword, email);
     }

    @Override
    public String resetPasswordByAdmin(Long userId) {
        return userSpiPort.resetPasswordByAdmin(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario"));
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        return userSpiPort.changePassword(userId, currentPassword, newPassword);
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO dto) {
        return userSpiPort.updateUser(userId, dto);
    }

    @Override
    public String uploadProfilePicture(MultipartFile file) {
        return "";
    }


}
