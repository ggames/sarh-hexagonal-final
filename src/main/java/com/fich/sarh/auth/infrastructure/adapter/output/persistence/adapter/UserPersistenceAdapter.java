package com.fich.sarh.auth.infrastructure.adapter.output.persistence.adapter;

import com.fich.sarh.auth.application.ports.output.persistence.UserRetrieveSpiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserSaveSpiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserUploadSpiPort;
import com.fich.sarh.auth.domain.model.UserDTO;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entities.UserEntity;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.mapper.UserMapper;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.repository.UserRepository;
import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@PersistenceAdapter
public class UserPersistenceAdapter implements UserRetrieveSpiPort, UserSaveSpiPort, UserUploadSpiPort {

    private static final String PHOTO_DIRECTORY = "uploads/profile-pictures/";

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final Path photoRoot;

    @Autowired
    public UserPersistenceAdapter(UserRepository userRepository) {
        this(userRepository, Paths.get(PHOTO_DIRECTORY));
    }

    UserPersistenceAdapter(UserRepository userRepository, Path photoRoot) {
        this.userRepository = userRepository;
        this.photoRoot = photoRoot.toAbsolutePath().normalize();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return this.userRepository.findAll().stream().map(user -> UserMapper.INSTANCE.toUserDTO(user)
        ).collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper.INSTANCE::toUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper.INSTANCE::toUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findUserByUsernameAndEmail(String query) {
        return this.userRepository.findUserByUsernameAndEmail(query).stream().map(
                UserMapper.INSTANCE::toUserDTO
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getPhotoByUsername(String username) {

        logger.info("USUARIO A BUSCAR ... " + username);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario '" + username + "' no encontrado"));

        String photoProfile = user.getProfilePicturePath();

        // Sin foto registrada en BD → retorna null para que el controller devuelva 404
        if (photoProfile == null || photoProfile.isBlank()) {
            logger.info("El usuario '{}' no tiene foto de perfil registrada", username);
            return null;
        }

        Path path = resolveStoredPhoto(photoProfile);
        if (path == null) {
            logger.warn("Ruta de foto no válida para el usuario '{}'", username);
            return null;
        }

        try {
            // Archivo físico no encontrado → retorna null en lugar de lanzar excepción
            if (!Files.exists(path)) {
                logger.warn("Archivo de foto no encontrado en disco: {}", path);
                return null;
            }

            return Files.readAllBytes(path);
        } catch (IOException e) {
            logger.error("Error al leer la foto del usuario '{}': {}", username, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id) {
        return userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id);
    }

    @Override
    public UserDTO saveUsername(UserDTO user) {
        UserEntity entity = UserMapper.INSTANCE.toUserEntity(user);
        UserEntity saved = userRepository.save(entity);
        return UserMapper.INSTANCE.toUserDTO(saved);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    @Override
    public String uploadProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = Optional.ofNullable(file.getOriginalFilename())
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(Paths::get)
                .map(Path::getFileName)
                .map(Path::toString)
                .orElse("avatar.jpg");
        Path filepath = photoRoot.resolve(UUID.randomUUID() + "_" + originalName).normalize();
        if (!filepath.startsWith(photoRoot)) {
            throw new BusinessRuleViolationException("Nombre de archivo no válido");
        }

        try {
            Files.createDirectories(photoRoot);
            Files.copy(file.getInputStream(), filepath);
        } catch (IOException e) {
            throw new IllegalStateException("Error al guardar la imagen de perfil", e);
        }

        return "/" + PHOTO_DIRECTORY + filepath.getFileName();
    }

    private Path resolveStoredPhoto(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return null;
        }
        String marker = "profile-pictures/";
        String normalized = storedPath.replace('\\', '/');
        int index = normalized.lastIndexOf(marker);
        String name = index >= 0 ? normalized.substring(index + marker.length()) : normalized;
        Path path = photoRoot.resolve(name).normalize();
        return path.startsWith(photoRoot) ? path : null;
    }
}
