
package mypackage.controller;

import java.util.List;
import java.util.Map;
import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.dto.UserResponseDTO;
import mypackage.dto.UserRegistrationDTO;
import mypackage.dto.UserProfileUpdateDTO;
import mypackage.dto.AdminProfileUpdateDTO;
import mypackage.model.User;
import mypackage.service.User_Service;

@RestController
@RequestMapping("/api/users")
public class User_Controller {

    @Autowired
    private User_Service userService;


    // 1. Register new citizen
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody UserRegistrationDTO request) {

        try {

            userService.registerUser(request);

            return ResponseEntity.ok(
                    Collections.singletonMap(
                            "message",
                            "Registration successful"
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Collections.singletonMap(
                            "message",
                            e.getMessage()
                    )
            );

        } catch (IllegalStateException e) {

            return ResponseEntity.status(500).body(
                    Collections.singletonMap(
                            "message",
                            e.getMessage()
                    )
            );
        }
    }


    // 2. Save user
    @PostMapping
    public ResponseEntity<UserResponseDTO> saveUser(
            @RequestBody User user) {

        return ResponseEntity.ok(
                userService.saveUser(user)
        );
    }


    // 3. Get all users
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {

        return userService.getAllUsers();
    }


    // 4. Get user by ID
    @GetMapping("/{userId:\\d+}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long userId) {

        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // 5. Update user by ID
    @PutMapping("/{userId:\\d+}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody User user) {

        return userService.updateUser(userId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // 6. Login with session management
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> loginData,
            HttpSession session) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || password == null ||
                email.trim().isEmpty() ||
                password.isEmpty()) {

            return ResponseEntity.badRequest().body(
                    Collections.singletonMap(
                            "message",
                            "Email and password are required"
                    )
            );
        }

        return userService.login(email, password)
                .<ResponseEntity<?>>map(user -> {

                    // Prevent session fixation on login
                    session.invalidate();

                    // Create a fresh session
                    HttpSession newSession =
                            ((org.springframework.web.context.request.ServletRequestAttributes)
                            org.springframework.web.context.request.RequestContextHolder
                                    .currentRequestAttributes())
                                    .getRequest().getSession(true);

                    newSession.setAttribute(
                            "loggedInUserId",
                            user.getUserId()
                    );

                    newSession.setAttribute(
                            "loggedInUserRole",
                            user.getRoleName()
                    );

                    return ResponseEntity.ok(user);
                })
                .orElse(
                    ResponseEntity.status(401).body(
                            Collections.singletonMap(
                                    "message",
                                    "Invalid email or password"
                            )
                    )
                );
    }


    // 7. Check logged-in user session
    @GetMapping("/session")
    public ResponseEntity<?> checkSession(
            HttpSession session) {

        Long userId = (Long) session.getAttribute(
                "loggedInUserId"
        );

        if (userId == null) {

            return ResponseEntity.status(401).body(
                    Collections.singletonMap(
                            "message",
                            "User is not logged in"
                    )
            );
        }

        return userService.getUserById(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(
                    ResponseEntity.status(401).body(
                            Collections.singletonMap(
                                    "message",
                                    "User session is invalid"
                            )
                    )
                );
    }


    // 8. Update logged-in citizen profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody UserProfileUpdateDTO request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute(
                "loggedInUserId"
        );

        String role = (String) session.getAttribute(
                "loggedInUserRole"
        );

        if (userId == null) {

            return ResponseEntity.status(401).body(
                    Collections.singletonMap(
                            "message",
                            "Please log in to update your profile"
                    )
            );
        }

        if (!"USER".equals(role)) {

            return ResponseEntity.status(403).body(
                    Collections.singletonMap(
                            "message",
                            "Only citizens can update this profile"
                    )
            );
        }

        try {

            UserResponseDTO updatedUser =
                    userService.updateMyProfile(
                            userId,
                            request
                    );

            return ResponseEntity.ok(updatedUser);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Collections.singletonMap(
                            "message",
                            e.getMessage()
                    )
            );
        }
    }


    // 9. Update logged-in Admin profile
    @PutMapping("/admin/profile")
    public ResponseEntity<?> updateAdminProfile(
            @RequestBody AdminProfileUpdateDTO request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute(
                "loggedInUserId"
        );

        String role = (String) session.getAttribute(
                "loggedInUserRole"
        );

        if (userId == null) {

            return ResponseEntity.status(401).body(
                    Collections.singletonMap(
                            "message",
                            "Please log in to update your profile"
                    )
            );
        }

        if (!"ADMIN".equals(role)) {

            return ResponseEntity.status(403).body(
                    Collections.singletonMap(
                            "message",
                            "Only admins can update this profile"
                    )
            );
        }

        try {

            UserResponseDTO updatedAdmin =
                    userService.updateAdminProfile(
                            userId,
                            request
                    );

            return ResponseEntity.ok(updatedAdmin);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Collections.singletonMap(
                            "message",
                            e.getMessage()
                    )
            );
        }
    }


    // 10. Delete user by ID
    @DeleteMapping("/{userId:\\d+}")
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


    // 11. Logout user
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok(
                Collections.singletonMap(
                        "message",
                        "Logout successful"
                )
        );
    }
}
