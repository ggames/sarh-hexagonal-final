package com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fich.sarh.auth.Domain.model.UserDTO;
import com.fich.sarh.auth.Domain.ports.inbound.UserApiPort;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.mapper.UserRestMapper;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserApiPort userApiPort;
    private final UserRestMapper restMapper;
    private final UserMapper mapper;
    //private final UserSaveApiPort userSave;

    private String file_path;





    @PreAuthorize("hasAnyRole('ADMIN','ONLY_CONSULT')")
    @GetMapping("{userId}")
    public ResponseEntity<?> fetchUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(restMapper.toUserResponse(userApiPort.findUserById(userId)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ONLY_CONSULT')")
    @GetMapping("all")
    public ResponseEntity<?> fetchAllUsers() {
        return ResponseEntity.ok().body(userApiPort.findAllUsers());
    }


    @PreAuthorize("hasAnyRole('ADMIN','ONLY_CONSULT')")
    @GetMapping("/search/{query}")
    public ResponseEntity<?> fetchUserByUsernameAndEmail(@PathVariable String query) {
        return ResponseEntity.ok().body(userApiPort.findUserByUsernameAndEmail(query));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createUser(@Valid @RequestPart("createUser") UserDTO createUser,
                                        @RequestPart(value = "file", required = false)
                                        MultipartFile file) throws JsonProcessingException {


    /*    Set<RoleDTO> roles = createUser.getRoles().stream()
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
                .roles(roles_entity).build();*/


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userApiPort.createUser(createUser,file ));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    @PutMapping(value = "/update/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @Validated @RequestPart("updateUser") UserDTO updateUser,
                                        @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {



        if (file != null && !file.isEmpty()) {
            String filename = storeProfileImage(file);
            updateUser.setProfilePicturePath(filename);
        }


        UserDTO saveUser = userApiPort.updateUser(userId, updateUser);

        return ResponseEntity.ok(saveUser);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN','DEVELOPER')")
    @GetMapping("{username}/photo")
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable String username) throws IOException {
        byte[] imageBytes = userApiPort.getPhotoByUsername(username);

        String contentType = Files.probeContentType(Paths.get("uploads/profile-pictures/" + username + ".jpg"));
        log.info("IMAGEN FOTO PERFIL " + imageBytes);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpg"))
                .body(imageBytes);
    }

    private String storeProfileImage(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String uploadsDir = "uploads/profile-pictures/";
                Path path = Paths.get(uploadsDir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filepath = path.resolve(filename);

                Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

                this.file_path = "/" + uploadsDir + filename;

            } catch (IOException e) {
                throw new RuntimeException("Error al guardar imagen de perfil");
            }

            }
        return this.file_path;

    }

}


 /*   @PostMapping("/createuser")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest createUser){

        Set<RoleRequest> roles = createUser.getRoles().stream().map(
                role -> RoleRequest.builder().name(role.getName()).build()
        ).collect(Collectors.toSet());

        UserDTO userDTO = UserDTO.builder()
                .username(createUser.getUsername())
                .password(createUser.getPassword())
                .email(createUser.getEmail())
                .roles(RoleRestMapper.INSTANCE.toRoleDTOList(roles)).build();

        userSave.saveUsername(userDTO);

        return ResponseEntity.ok(userDTO);
    }*/



