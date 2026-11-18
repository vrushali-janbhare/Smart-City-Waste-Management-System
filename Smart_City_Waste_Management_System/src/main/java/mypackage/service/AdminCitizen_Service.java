
package mypackage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.dto.UserResponseDTO;
import mypackage.model.User;
import mypackage.repository.User_Repository;

@Service
public class AdminCitizen_Service {

    @Autowired
    private User_Repository userRepository;

    // Retrieve all registered citizens
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllCitizens() {

        List<User> citizens =
                userRepository.findByRole_RoleNameOrderByUserIdDesc("USER");

        return citizens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert entity into safe response DTO
    private UserResponseDTO convertToDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setRoleName(user.getRole().getRoleName());

        return dto;
    }
}
