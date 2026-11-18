
package mypackage.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Area;
import mypackage.model.BulkPickupRequest;
import mypackage.model.User;
import mypackage.model.WasteCategory;

import mypackage.repository.Area_Repository;
import mypackage.repository.BulkPickupRequest_Repository;
import mypackage.repository.User_Repository;
import mypackage.repository.WasteCategory_Repository;

@Service
public class BulkPickupRequest_Service {

    @Autowired
    private BulkPickupRequest_Repository bulkPickupRequestRepository;

    @Autowired
    private User_Repository userRepository;

    @Autowired
    private Area_Repository areaRepository;

    @Autowired
    private WasteCategory_Repository wasteCategoryRepository;

    // 1. Create a new bulk pickup request
    public BulkPickupRequest createRequest(
            Long userId,
            Long areaId,
            Long categoryId,
            LocalDate requestedDate,
            String pickupAddress,
            BigDecimal estimatedQuantityKg) {

        // Validate user
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required");
        }

        // Validate area and category
        if (areaId == null || categoryId == null) {
            throw new IllegalArgumentException(
                    "Area and waste category are required");
        }

        // Validate pickup date
        if (requestedDate == null ||
                requestedDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Pickup date cannot be in the past");
        }

        // Validate pickup address
        if (pickupAddress == null ||
                pickupAddress.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Pickup address is required");
        }

        String address = pickupAddress.trim();

        if (address.length() > 255) {
            throw new IllegalArgumentException(
                    "Pickup address cannot exceed 255 characters");
        }

        // Validate estimated quantity
        if (estimatedQuantityKg == null ||
                estimatedQuantityKg.compareTo(BigDecimal.ZERO) <= 0 ||
                estimatedQuantityKg.scale() > 2 ||
                estimatedQuantityKg.compareTo(
                        new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException(
                    "Enter a valid quantity greater than zero");
        }

        // Find logged-in user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        // Find selected area
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Selected area not found"));

        // Find selected waste category
        WasteCategory category =
                wasteCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Selected category not found"));

        // Create new request
        BulkPickupRequest request = new BulkPickupRequest();

        request.setUser(user);
        request.setArea(area);
        request.setCategory(category);
        request.setRequestedDate(requestedDate);
        request.setPickupAddress(address);
        request.setEstimatedQuantityKg(estimatedQuantityKg);

        // Automatically assign PENDING status
        request.setRequestStatus("PENDING");

        // Save request to MySQL
        return bulkPickupRequestRepository.save(request);
    }

    // 2. Get logged-in citizen's requests
    public List<BulkPickupRequest> getMyRequests(Long userId) {

        return bulkPickupRequestRepository
                .findByUserUserIdOrderByRequestIdDesc(userId);
    }

    // 3. Get a request belonging to the citizen
    public Optional<BulkPickupRequest> getMyRequestById(
            Long requestId, Long userId) {

        return bulkPickupRequestRepository.findById(requestId)
                .filter(request ->
                        request.getUser()
                                .getUserId()
                                .equals(userId));
    }
}
