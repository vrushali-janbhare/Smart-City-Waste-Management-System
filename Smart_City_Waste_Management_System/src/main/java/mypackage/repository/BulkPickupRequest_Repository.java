
package mypackage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mypackage.model.BulkPickupRequest;

public interface BulkPickupRequest_Repository
        extends JpaRepository<BulkPickupRequest, Long> {

    List<BulkPickupRequest> findByUserUserIdOrderByRequestIdDesc(
            Long userId);
}
