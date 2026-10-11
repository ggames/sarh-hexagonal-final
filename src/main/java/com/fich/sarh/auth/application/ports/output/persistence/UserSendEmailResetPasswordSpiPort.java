package com.fich.sarh.auth.application.ports.output.persistence;




public interface UserSendEmailResetPasswordSpiPort {

    void sendEmailResetPassword(String newPassword, String email) ;
}
