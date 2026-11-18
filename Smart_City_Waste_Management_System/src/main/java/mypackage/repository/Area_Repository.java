
package mypackage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.Area;

@Repository
public interface Area_Repository extends JpaRepository<Area, Long> {

    Optional<Area> findByWardNumberIgnoreCase(String wardNumber);

}
