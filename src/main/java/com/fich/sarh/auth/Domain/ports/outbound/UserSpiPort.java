package com.fich.sarh.auth.Domain.ports.outbound;

import com.fich.sarh.auth.Domain.model.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserSpiPort  {


    UserDTO saveUser(UserDTO userCreate);

    List<UserDTO> findAllUsers();
    List<UserDTO> findUserByUsernameAndEmail(String query);
    Optional<UserDTO> findByUsername(String username);
    Optional<UserDTO> findUserById(Long userId);
    byte[] getPhotoByUsername(String username);
    boolean existsUsername(String username);
    void sendEmailResetPassword(String newPassword, String email) ;

    Optional<String> resetPasswordByAdmin(Long userId);

    boolean changePassword(Long userId, String currentPassword, String newPassword);

    UserDTO updateUser(Long userId, UserDTO dto);

    String uploadProfilePicture(MultipartFile file);

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
