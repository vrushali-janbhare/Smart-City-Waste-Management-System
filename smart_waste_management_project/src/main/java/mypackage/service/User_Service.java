package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.User;
import mypackage.repository.User_Repository;

@Service
public class User_Service {

    @Autowired
    private User_Repository userRepository;

    // Add User
    public User addUser(User user) {
        return userRepository.save(user);
    }

    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get User By ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Update User
    public User updateUser(Long id, User user) {

        User existingUser = userRepository.findById(id).orElse(null);

        if (existingUser != null) {

            existingUser.setFull_name(user.getFull_name());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setPassword(user.getPassword());
            existingUser.setRole(user.getRole());
            existingUser.setAddress(user.getAddress());
            existingUser.setIs_active(user.isIs_active());
            existingUser.setUpdated_at(user.getUpdated_at());
            existingUser.setAreas(user.getAreas());

            return userRepository.save(existingUser);
        }

        return null;
    }

    // Delete User
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}