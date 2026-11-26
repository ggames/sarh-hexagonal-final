package com.fich.sarh.auth.application.services;

import com.fich.sarh.auth.application.ports.entrypoint.api.UserUploadApiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserUploadSpiPort;
import com.fich.sarh.common.UseCase;
import org.springframework.web.multipart.MultipartFile;

@UseCase
public class UserUploadUseCase implements UserUploadApiPort {

    private final UserUploadSpiPort userUploadSpiPort;

    public UserUploadUseCase(UserUploadSpiPort userUploadSpiPort) {
        this.userUploadSpiPort = userUploadSpiPort;
    }

    @Override
    public String uploadProfilePicture(MultipartFile file) {
        return userUploadSpiPort.uploadProfilePicture(file);
    }
}
