
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Role;
import mypackage.service.Role_Service;

@RestController
@RequestMapping("/api/roles")
public class Role_Controller {

    @Autowired
    private Role_Service roleService;

    // Insert new role
    @PostMapping
    public Role saveRole(@RequestBody Role role) {
        return roleService.saveRole(role);
    }

    // Get all roles
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // Get role by ID
    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(
            @PathVariable Long roleId) {

        return roleService.getRoleById(roleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update role by ID
    @PutMapping("/{roleId}")
    public ResponseEntity<Role> updateRole(
            @PathVariable Long roleId,
            @RequestBody Role role) {

        return roleService.getRoleById(roleId)
                .map(existingRole -> {
                    role.setRoleId(roleId);
                    return ResponseEntity.ok(
                            roleService.saveRole(role));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete role by ID
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable Long roleId) {

        return roleService.getRoleById(roleId)
                .map(existingRole -> {
                    roleService.deleteRole(roleId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
