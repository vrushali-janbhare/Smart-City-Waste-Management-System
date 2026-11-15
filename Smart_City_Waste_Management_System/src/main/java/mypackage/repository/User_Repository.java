
package mypackage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.User;

@Repository
public interface User_Repository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
