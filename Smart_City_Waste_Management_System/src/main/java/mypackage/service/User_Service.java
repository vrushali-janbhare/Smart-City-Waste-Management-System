
package mypackage.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mypackage.model.User;
import mypackage.dto.UserResponseDTO;
import mypackage.repository.User_Repository;

@Service
public class User_Service {

    @Autowired
    private User_Repository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // Convert User entity into UserResponseDTO
    private UserResponseDTO convertToDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());

        return dto;
    }


    // 1. Insert new user with encrypted password
    public UserResponseDTO saveUser(User user) {

        if (user.getPassword() == null ||
                user.getPassword().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        // Encrypt (hash) password
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }


    // 2. Get all users
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // 3. Get user by ID
    public Optional<UserResponseDTO> getUserById(Long userId) {

        return userRepository.findById(userId)
                .map(this::convertToDTO);
    }


    // 4. Update existing user safely
    public Optional<UserResponseDTO> updateUser(
            Long userId, User updatedUser) {

        return userRepository.findById(userId)
                .map(existingUser -> {

                    existingUser.setFullName(
                            updatedUser.getFullName()
                    );

                    existingUser.setEmail(
                            updatedUser.getEmail()
                    );

                    existingUser.setPhoneNumber(
                            updatedUser.getPhoneNumber()
                    );

                    existingUser.setAddress(
                            updatedUser.getAddress()
                    );

                    // Update role only if provided
                    if (updatedUser.getRole() != null) {
                        existingUser.setRole(
                                updatedUser.getRole()
                        );
                    }

                    // Update password only if a new password is provided
                    if (updatedUser.getPassword() != null &&
                            !updatedUser.getPassword().trim().isEmpty()) {

                        existingUser.setPassword(
                                passwordEncoder.encode(
                                        updatedUser.getPassword()
                                )
                        );
                    }

                    User savedUser =
                            userRepository.save(existingUser);

                    return convertToDTO(savedUser);
                });
    }


    // 5. Login using email and password
    public Optional<UserResponseDTO> login(
            String email, String enteredPassword) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (!optionalUser.isPresent()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        boolean passwordMatches =
                passwordEncoder.matches(
                        enteredPassword,
                        user.getPassword()
                );

        if (!passwordMatches) {
            return Optional.empty();
        }

        return Optional.of(convertToDTO(user));
    }


    // 6. Delete user
    public void deleteUser(Long userId) {

        userRepository.deleteById(userId);
    }
}
