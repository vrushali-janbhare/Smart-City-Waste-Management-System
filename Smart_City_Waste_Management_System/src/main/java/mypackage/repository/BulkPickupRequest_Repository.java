
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.BulkPickupRequest;

public interface BulkPickupRequest_Repository extends JpaRepository<BulkPickupRequest, Long> {

}
