
package mypackage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mypackage.model.BulkPickupRequest;

public interface BulkPickupRequest_Repository
        extends JpaRepository<BulkPickupRequest, Long> {

    // Citizen: only their own requests
    List<BulkPickupRequest> findByUserUserIdOrderByRequestIdDesc(
            Long userId);

    // Admin: all requests, newest first
    List<BulkPickupRequest> findAllByOrderByRequestIdDesc();
}
