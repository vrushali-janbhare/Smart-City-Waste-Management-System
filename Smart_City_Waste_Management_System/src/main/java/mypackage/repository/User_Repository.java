
package mypackage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.User;

@Repository
public interface User_Repository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    // Count only citizens
    long countByRole_RoleName(String roleName);

    // Retrieve citizens only
    List<User> findByRole_RoleNameOrderByUserIdDesc(String roleName);
}
