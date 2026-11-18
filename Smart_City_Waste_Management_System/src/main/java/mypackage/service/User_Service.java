
package mypackage.service;

import java.util.List;
import java.util.Optional;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.model.User;
import mypackage.model.Role;

import mypackage.dto.UserResponseDTO;
import mypackage.dto.UserRegistrationDTO;
import mypackage.dto.UserProfileUpdateDTO;

import mypackage.repository.User_Repository;
import mypackage.repository.Role_Repository;

@Service
public class User_Service {

    @Autowired
    private User_Repository userRepository;

    @Autowired
    private Role_Repository roleRepository;

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

        dto.setRoleName(
                user.getRole().getRoleName()
        );

        return dto;
    }


    // 1. Save user
    public UserResponseDTO saveUser(User user) {

        if (user.getPassword() == null ||
                user.getPassword().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

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


    // 4. Existing general update method
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

                    if (updatedUser.getRole() != null) {
                        existingUser.setRole(
                                updatedUser.getRole()
                        );
                    }

                    if (updatedUser.getPassword() != null &&
                            !updatedUser.getPassword()
                                    .trim().isEmpty()) {

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


    // 5. Login
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


    // 7. Register new citizen
    public UserResponseDTO registerUser(
            UserRegistrationDTO request) {

        if (request.getFullName() == null ||
                request.getEmail() == null ||
                request.getPhoneNumber() == null ||
                request.getAddress() == null ||
                request.getPassword() == null ||
                request.getConfirmPassword() == null) {

            throw new IllegalArgumentException(
                    "All fields are required"
            );
        }

        String fullName = request.getFullName().trim();

        String email = request.getEmail()
                .trim().toLowerCase(Locale.ROOT);

        String phone = request.getPhoneNumber().trim();

        String address = request.getAddress().trim();

        String password = request.getPassword();

        if (fullName.isEmpty() ||
                email.isEmpty() ||
                phone.isEmpty() ||
                address.isEmpty() ||
                password.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "All fields are required"
            );
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }

        if (!password.equals(
                request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        if (userRepository.findByPhoneNumber(phone).isPresent()) {
            throw new IllegalArgumentException(
                    "Phone number is already registered"
            );
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException(
                        "USER role not found in database"
                ));

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setAddress(address);

        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setRole(userRole);

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }


    // 8. NEW - Update logged-in citizen profile
    @Transactional
    public UserResponseDTO updateMyProfile(
            Long userId,
            UserProfileUpdateDTO request) {

        if (request == null ||
                request.getFullName() == null ||
                request.getEmail() == null ||
                request.getPhoneNumber() == null ||
                request.getAddress() == null) {

            throw new IllegalArgumentException(
                    "All profile fields are required"
            );
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User account not found"
                ));

        if (existingUser.getRole() == null ||
                !"USER".equals(
                        existingUser.getRole().getRoleName())) {

            throw new IllegalArgumentException(
                    "Only citizens can update this profile"
            );
        }

        String fullName = request.getFullName().trim();

        String email = request.getEmail().trim()
                .toLowerCase(Locale.ROOT);

        String phone = request.getPhoneNumber().trim();

        String address = request.getAddress().trim();

        if (fullName.isEmpty() ||
                email.isEmpty() ||
                phone.isEmpty() ||
                address.isEmpty()) {

            throw new IllegalArgumentException(
                    "All profile fields are required"
            );
        }

        if (fullName.length() > 100 ||
                email.length() > 255 ||
                phone.length() > 20 ||
                address.length() > 255) {

            throw new IllegalArgumentException(
                    "One or more profile fields are too long"
            );
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address"
            );
        }

        if (!phone.matches("^[0-9+() -]{7,20}$")) {
            throw new IllegalArgumentException(
                    "Enter a valid phone number"
            );
        }

        Optional<User> emailUser =
                userRepository.findByEmail(email);

        if (emailUser.isPresent() &&
                !emailUser.get().getUserId()
                        .equals(userId)) {

            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        Optional<User> phoneUser =
                userRepository.findByPhoneNumber(phone);

        if (phoneUser.isPresent() &&
                !phoneUser.get().getUserId()
                        .equals(userId)) {

            throw new IllegalArgumentException(
                    "Phone number is already registered"
            );
        }

        existingUser.setFullName(fullName);
        existingUser.setEmail(email);
        existingUser.setPhoneNumber(phone);
        existingUser.setAddress(address);

        // Password and role are not changed
        User savedUser =
                userRepository.save(existingUser);

        return convertToDTO(savedUser);
    }
}
