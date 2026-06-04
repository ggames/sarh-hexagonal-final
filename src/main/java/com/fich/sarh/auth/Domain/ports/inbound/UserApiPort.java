package com.fich.sarh.auth.Domain.ports.inbound;

import com.fich.sarh.auth.Domain.model.UserDTO;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.response.AuthResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserApiPort {


    AuthResponse createUser(UserDTO user, MultipartFile file);

  //    AuthResponse saveUser(UserDTO request);
    List<UserDTO> findAllUsers();
    List<UserDTO> findUserByUsernameAndEmail(String query);
    UserDTO findByUsername(String username);
    UserDTO findUserById(Long userId);
    byte[] getPhotoByUsername(String username);
    boolean existsUsername(String username);
    void sendEmailResetPassword(String newPassword, String email) ;


    // =====================================================
    //  Reset and Change Password
    // =====================================================
    String  resetPasswordByAdmin(Long userId);
    boolean changePassword(Long userId, String currentPassword, String newPassword);

    UserDTO updateUser(Long userId, UserDTO dto);

    String uploadProfilePicture(MultipartFile file);

}
