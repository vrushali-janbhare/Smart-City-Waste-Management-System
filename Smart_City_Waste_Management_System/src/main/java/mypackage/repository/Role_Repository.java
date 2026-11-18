
package mypackage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.Role;

@Repository
public interface Role_Repository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

}
