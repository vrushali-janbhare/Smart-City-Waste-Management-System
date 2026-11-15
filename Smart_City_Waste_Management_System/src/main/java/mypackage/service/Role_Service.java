
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Role;
import mypackage.repository.Role_Repository;

@Service
public class Role_Service {

    @Autowired
    private Role_Repository roleRepository;

    // Insert or update role
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    // Get all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Get role by ID
    public Optional<Role> getRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    // Delete role by ID
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }
}
