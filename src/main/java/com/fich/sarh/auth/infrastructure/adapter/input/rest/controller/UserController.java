package com.fich.sarh.auth.infrastructure.adapter.input.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fich.sarh.auth.application.ports.entrypoint.api.RoleRetrieveApiPort;
import com.fich.sarh.auth.application.ports.entrypoint.api.UserSaveApiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserRetrieveSpiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserUpdateSpiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserUploadSpiPort;
import com.fich.sarh.auth.domain.model.RoleDTO;
import com.fich.sarh.auth.domain.model.UserDTO;
import com.fich.sarh.auth.infrastructure.adapter.input.rest.mapper.UserRestMapper;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entities.RoleEntity;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entities.UserEntity;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.mapper.RoleMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserSaveApiPort userSave;

    private final UserUploadSpiPort userUploadSpiPort;
    private final PasswordEncoder passwordEncoder;
  //  private final UserResetPasswordSpiPort passwordService;

    private final RoleRetrieveApiPort roleRetrieveApiPort;

    private final UserRetrieveSpiPort userRetrieveSpiPort;

    private final UserUpdateSpiPort userUpdateSpiPort;

    Logger logger = LoggerFactory.getLogger(getClass());




    @PreAuthorize("hasAnyRole('ADMIN','ONLY_CONSULT')")
    @GetMapping("{userId}")
    public ResponseEntity<?> fetchUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(UserRestMapper.INSTANCE.toUserResponse(userRetrieveSpiPort.findUserById(userId).get()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ONLY_CONSULT')")
    @GetMapping("all")
    public ResponseEntity<?> fetchAllUsers() {
        return ResponseEntity.ok().body(userRetrieveSpiPort.findAllUsers());
    }


    @PreAuthorize("hasAnyRole('ADMIN','ONLY_CONSULT')")
    @GetMapping("/search/{query}")
    public ResponseEntity<?> fetchUserByUsernameAndEmail(@PathVariable String query) {
        return ResponseEntity.ok().body(userRetrieveSpiPort.findUserByUsernameAndEmail(query));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createUser(@Valid @RequestPart("createUser") UserDTO createUser,
                                        @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {


        Set<RoleDTO> roles = createUser.getRoles().stream()
                .map(role -> {
                    return roleRetrieveApiPort.fetchByRoleEnum(role.getRoleEnum());
                })
                .collect(Collectors.toSet());

        Set<RoleEntity> roles_entity = RoleMapper.INSTANCE.toEntityList(roles);


        logger.info("ROLES " + roles_entity);

        String filename = "";
        if (file != null && !file.isEmpty()) {
            filename = userUploadSpiPort.uploadProfilePicture(file);
        }
        logger.info("ARCHIVO " + filename);
        UserEntity userEntity = UserEntity.builder()
                .username(createUser.getUsername())
                .password(passwordEncoder.encode(createUser.getPassword()))
                .email(createUser.getEmail())
                .profilePicturePath(filename)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialNonExpired(true)
                .roles(roles_entity).build();


        return ResponseEntity.status(HttpStatus.CREATED).body(userSave.saveUsername(userEntity));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    @PutMapping(value = "/update/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @Validated @RequestPart("updateUser") UserDTO updateUser,
                                        @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        logger.info("Imagen " + file);

        if (file != null && !file.isEmpty()) {
            String filename = storeProfileImage(file);
            updateUser.setProfilePicturePath(filename);
        }


        UserDTO saveUser = userUpdateSpiPort.updateUser(userId, updateUser);

        return ResponseEntity.ok(saveUser);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN','DEVELOPER')")
    @GetMapping("{username}/photo")
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable String username) throws IOException {
        if (username == null || !username.matches("^[a-zA-Z0-9._-]+$")) {
            return ResponseEntity.badRequest().build();
        }

        byte[] imageBytes = userRetrieveSpiPort.getPhotoByUsername(username);
        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        Path basePath = Paths.get("uploads/profile-pictures").toAbsolutePath().normalize();
        Path filePath = basePath.resolve(username + ".jpg").normalize();

        String contentType = "image/jpeg";
        if (filePath.startsWith(basePath) && Files.exists(filePath)) {
            String probed = Files.probeContentType(filePath);
            if (probed != null) {
                contentType = probed;
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }

    private String storeProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path uploadsDir = Paths.get("uploads/profile-pictures").toAbsolutePath().normalize();
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir);
            }
            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "avatar.jpg";
            String cleanName = Paths.get(originalName).getFileName().toString();
            String filename = UUID.randomUUID() + "_" + cleanName;
            Path filepath = uploadsDir.resolve(filename).normalize();

            if (!filepath.startsWith(uploadsDir)) {
                throw new IllegalArgumentException("Nombre de archivo inválido");
            }

            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/profile-pictures/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar imagen de perfil: " + e.getMessage());
        }
    }

}



