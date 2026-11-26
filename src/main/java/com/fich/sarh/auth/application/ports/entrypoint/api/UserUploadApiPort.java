package com.fich.sarh.auth.application.ports.entrypoint.api;

import org.springframework.web.multipart.MultipartFile;

public interface UserUploadApiPort {

    String uploadProfilePicture(MultipartFile file);
}
