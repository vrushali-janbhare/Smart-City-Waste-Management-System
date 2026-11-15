
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.Vehicle;

public interface Vehicle_Repository extends JpaRepository<Vehicle, Long> {

}
