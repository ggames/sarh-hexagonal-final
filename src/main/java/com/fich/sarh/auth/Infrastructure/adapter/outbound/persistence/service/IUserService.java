package com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.service;

import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    void sendEmailResetPassword(String newPassword, String email) ;

    public String uploadProfilePicture(MultipartFile file);
}
