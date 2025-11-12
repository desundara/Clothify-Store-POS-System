package edu.icet.service.interfaces;

import edu.icet.model.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(String userId);
    boolean updateUser(UserDTO user);
    boolean deleteUser(String userId);
    List<UserDTO> getUsersByRole(String role);
}