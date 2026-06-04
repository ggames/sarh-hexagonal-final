package com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.service;

import com.fich.sarh.auth.Infrastructure.adapter.configuration.email.MailProperties;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private String file_path;

    @Override
    public void sendEmailResetPassword(String newPassword, String email) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(mailProperties.getFrom());
            helper.setTo(email);
            helper.setSubject("Reestablecimiento de Contraseña");
            helper.setText(buildMessage(newPassword));
            mailSender.send(message);


        }catch (Exception e){
            throw new IllegalStateException("Error enviando correo de reseteo ", e);
        }
    }

    @Override
    public String uploadProfilePicture(MultipartFile file) {
       if(file != null && !file.isEmpty() ){
           try{
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
               throw new RuntimeException(e);
           }
       }

        return "";
    }

    private String buildMessage(String newPassword) {
        return """
               Su contraseña ha sido restablecida correctamente.

               Contraseña provisoria: %s

               Por favor, cambie su contraseña al iniciar sesión.
               """.formatted(newPassword);
    }
}
