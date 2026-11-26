package com.fich.sarh.auth.infrastructure.adapter.output.persistence.adapter;

import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entities.UserEntity;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.repository.UserRepository;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserPersistenceAdapterPhotoPathTest {

    @TempDir
    Path tempDir;

    @Test
    void uploadSanitizesTraversalAttemptInOriginalFilename() throws Exception {
        UserPersistenceAdapter adapter = new UserPersistenceAdapter(mock(UserRepository.class), tempDir);
        MockMultipartFile file = new MockMultipartFile(
                "file", "../../../../etc/passwd", "image/png", new byte[]{1, 2, 3});

        String storedPath = adapter.uploadProfilePicture(file);

        assertThat(storedPath).startsWith("/uploads/profile-pictures/");
        assertThat(storedPath).doesNotContain("..");
        try (var files = Files.list(tempDir)) {
            var names = files.map(p -> p.getFileName().toString()).toList();
            assertThat(names).hasSize(1);
            assertThat(names.get(0)).doesNotContain("..");
        }
    }

    @Test
    void uploadRejectsBlankFilename() {
        UserPersistenceAdapter adapter = new UserPersistenceAdapter(mock(UserRepository.class), tempDir);
        MockMultipartFile file = new MockMultipartFile("file", "   ", "image/png", new byte[]{1});

        assertThat(adapter.uploadProfilePicture(file)).contains("avatar.jpg").doesNotContain("..");
    }

    @Test
    void getPhotoRefusesStoredPathOutsidePhotoRoot() {
        UserRepository repository = mock(UserRepository.class);
        UserEntity user = UserEntity.builder().profilePicturePath("../../etc/passwd").build();
        when(repository.findByUsername("alice")).thenReturn(Optional.of(user));
        UserPersistenceAdapter adapter = new UserPersistenceAdapter(repository, tempDir);

        assertThat(adapter.getPhotoByUsername("alice")).isNull();
    }

    @Test
    void getPhotoRefusesAbsolutePath() throws Exception {
        Path secret = tempDir.resolveSibling("secret.png");
        Files.write(secret, new byte[]{9});
        UserRepository repository = mock(UserRepository.class);
        UserEntity user = UserEntity.builder().profilePicturePath(secret.toString()).build();
        when(repository.findByUsername("bob")).thenReturn(Optional.of(user));
        UserPersistenceAdapter adapter = new UserPersistenceAdapter(repository, tempDir);

        assertThat(adapter.getPhotoByUsername("bob")).isNull();
        Files.deleteIfExists(secret);
    }
}
