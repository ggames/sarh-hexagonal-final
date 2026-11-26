package com.fich.sarh.auth.infrastructure.adapter.input.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fich.sarh.auth.application.ports.entrypoint.api.RoleRetrieveApiPort;
import com.fich.sarh.auth.application.ports.entrypoint.api.UserRetrieveApiPort;
import com.fich.sarh.auth.application.ports.entrypoint.api.UserSaveApiPort;
import com.fich.sarh.auth.application.ports.entrypoint.api.UserUpdateApiPort;
import com.fich.sarh.auth.application.ports.entrypoint.api.UserUploadApiPort;
import com.fich.sarh.auth.domain.model.RoleDTO;
import com.fich.sarh.auth.domain.model.UserDTO;
import com.fich.sarh.auth.infrastructure.adapter.input.rest.mapper.UserRestMapper;
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
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserSaveApiPort userSave;
    private final UserUploadApiPort userUploadApiPort;
    private final PasswordEncoder passwordEncoder;
    private final RoleRetrieveApiPort roleRetrieveApiPort;
    private final UserRetrieveApiPort userRetrieveApiPort;
    private final UserUpdateApiPort userUpdateApiPort;

    Logger logger = LoggerFactory.getLogger(getClass());

    @PreAuthorize("hasAnyRole('ADMIN','ONLY_CONSULT')")
    @GetMapping("{userId}")
    public ResponseEntity<?> fetchUserById(@PathVariable Long userId) {
        return userRetrieveApiPort.findUserById(userId)
                .map(UserRestMapper.INSTANCE::toUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ONLY_CONSULT')")
    @GetMapping("all")
    public ResponseEntity<?> fetchAllUsers() {
        return ResponseEntity.ok().body(userRetrieveApiPort.findAllUsers());
    }

    @PreAuthorize("hasAnyRole('ADMIN','ONLY_CONSULT')")
    @GetMapping("/search/{query}")
    public ResponseEntity<?> fetchUserByUsernameAndEmail(@PathVariable String query) {
        return ResponseEntity.ok().body(userRetrieveApiPort.findUserByUsernameAndEmail(query));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createUser(@Valid @RequestPart("createUser") UserDTO createUser,
                                        @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        Set<RoleDTO> roles = createUser.getRoles().stream()
                .map(role -> roleRetrieveApiPort.fetchByRoleEnum(role.getRoleEnum()))
                .collect(Collectors.toSet());

        String filename = "";
        if (file != null && !file.isEmpty()) {
            filename = userUploadApiPort.uploadProfilePicture(file);
        }
        logger.info("ARCHIVO " + filename);

        createUser.setPassword(passwordEncoder.encode(createUser.getPassword()));
        createUser.setProfilePicturePath(filename);
        createUser.setEnabled(true);
        createUser.setAccountNonExpired(true);
        createUser.setAccountNonLocked(true);
        createUser.setCredentialNonExpired(true);
        createUser.setRoles(roles);

        return ResponseEntity.status(HttpStatus.CREATED).body(userSave.saveUsername(createUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/update/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @Validated @RequestPart("updateUser") UserDTO updateUser,
                                        @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        logger.info("Imagen " + file);

        if (file != null && !file.isEmpty()) {
            String filename = userUploadApiPort.uploadProfilePicture(file);
            updateUser.setProfilePicturePath(filename);
        }

        UserDTO saveUser = userUpdateApiPort.updateUser(userId, updateUser);
        return ResponseEntity.ok(saveUser);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN','ONLY_CONSULT')")
    @GetMapping("{username}/photo")
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable String username) throws IOException {
        if (username == null || !username.matches("^[a-zA-Z0-9._-]+$")) {
            return ResponseEntity.badRequest().build();
        }

        byte[] imageBytes = userRetrieveApiPort.getPhotoByUsername(username);
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = {"{userId}", "delete/{userId}"})
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userSave.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
