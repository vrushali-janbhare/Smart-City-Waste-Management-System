
package mypackage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.Vehicle;

@Repository
public interface Vehicle_Repository
        extends JpaRepository<Vehicle, Long> {

    // Find vehicle by registration number,
    // ignoring uppercase/lowercase differences.
    Optional<Vehicle> findByVehicleNumberIgnoreCase(
            String vehicleNumber);
}
