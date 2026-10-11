package com.fich.sarh.auth.application.ports.output.persistence;

import org.springframework.web.multipart.MultipartFile;

public interface UserUploadSpiPort {

    String uploadProfilePicture(MultipartFile file);
}
