
package mypackage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.dto.UserResponseDTO;
import mypackage.model.User;
import mypackage.service.User_Service;

@RestController
@RequestMapping("/api/users")
public class User_Controller {

    @Autowired
    private User_Service userService;


    // 1. Register new user
    @PostMapping
    public ResponseEntity<UserResponseDTO> saveUser(
            @RequestBody User user) {

        return ResponseEntity.ok(
                userService.saveUser(user)
        );
    }


    // 2. Get all users
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {

        return userService.getAllUsers();
    }


    // 3. Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long userId) {

        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // 4. Update user by ID
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody User user) {

        return userService.updateUser(userId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // 5. User Login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> loginData) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || password == null ||
                email.trim().isEmpty() || password.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Email and password are required");
        }

        return userService.login(email, password)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(
                    ResponseEntity.status(401)
                            .body("Invalid email or password")
                );
    }


    // 6. Delete user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId) {

        return userService.getUserById(userId)
                .map(existingUser -> {
                    userService.deleteUser(userId);
                    return ResponseEntity.noContent()
                            .<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
