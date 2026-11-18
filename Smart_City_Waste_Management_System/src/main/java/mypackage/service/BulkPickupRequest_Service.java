
package mypackage.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 1. Citizen: Create request
    public BulkPickupRequest createRequest(
            Long userId,
            Long areaId,
            Long categoryId,
            LocalDate requestedDate,
            String pickupAddress,
            BigDecimal estimatedQuantityKg) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required");
        }

        if (areaId == null || categoryId == null) {
            throw new IllegalArgumentException(
                    "Area and waste category are required");
        }

        if (requestedDate == null ||
                requestedDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Pickup date cannot be in the past");
        }

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

        if (estimatedQuantityKg == null ||
                estimatedQuantityKg.compareTo(BigDecimal.ZERO) <= 0 ||
                estimatedQuantityKg.scale() > 2 ||
                estimatedQuantityKg.compareTo(
                        new BigDecimal("99999999.99")) > 0) {

            throw new IllegalArgumentException(
                    "Enter a valid quantity greater than zero");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Selected area not found"));

        WasteCategory category =
                wasteCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Selected category not found"));

        BulkPickupRequest request = new BulkPickupRequest();

        request.setUser(user);
        request.setArea(area);
        request.setCategory(category);
        request.setRequestedDate(requestedDate);
        request.setPickupAddress(address);
        request.setEstimatedQuantityKg(estimatedQuantityKg);
        request.setRequestStatus("PENDING");

        return bulkPickupRequestRepository.save(request);
    }

    // 2. Citizen: View own requests
    public List<BulkPickupRequest> getMyRequests(Long userId) {

        return bulkPickupRequestRepository
                .findByUserUserIdOrderByRequestIdDesc(userId);
    }

    // 3. Citizen: View own request by ID
    public Optional<BulkPickupRequest> getMyRequestById(
            Long requestId,
            Long userId) {

        return bulkPickupRequestRepository.findById(requestId)
                .filter(request ->
                        request.getUser()
                                .getUserId()
                                .equals(userId));
    }

    // Convert request to Admin response
    private Map<String, Object> toAdminResponse(
            BulkPickupRequest request) {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("requestId", request.getRequestId());

        result.put("userId",
                request.getUser().getUserId());

        result.put("citizenName",
                request.getUser().getFullName());

        result.put("areaId",
                request.getArea().getAreaId());

        result.put("areaName",
                request.getArea().getAreaName());

        result.put("categoryId",
                request.getCategory().getCategoryId());

        result.put("categoryName",
                request.getCategory().getCategoryName());

        result.put("requestedDate",
                request.getRequestedDate());

        result.put("pickupAddress",
                request.getPickupAddress());

        result.put("estimatedQuantityKg",
                request.getEstimatedQuantityKg());

        result.put("requestStatus",
                request.getRequestStatus());

        return result;
    }

    // 4. Admin: View all requests
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllRequestsForAdmin() {

        List<BulkPickupRequest> requests =
                bulkPickupRequestRepository
                        .findAllByOrderByRequestIdDesc();

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (BulkPickupRequest request : requests) {
            result.add(toAdminResponse(request));
        }

        return result;
    }

    // 5. Admin: View one request
    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> getRequestForAdmin(
            Long requestId) {

        return bulkPickupRequestRepository.findById(requestId)
                .map(this::toAdminResponse);
    }

    // 6. Admin: Update request status
    @Transactional
    public Map<String, Object> updateRequestStatus(
            Long requestId,
            String newStatus) {

        if (requestId == null) {
            throw new IllegalArgumentException(
                    "Request ID is required");
        }

        if (newStatus == null ||
                newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Request status is required");
        }

        String status = newStatus.trim().toUpperCase(
                java.util.Locale.ROOT);

        if (!status.equals("APPROVED") &&
                !status.equals("REJECTED") &&
                !status.equals("IN_PROGRESS") &&
                !status.equals("COMPLETED")) {

            throw new IllegalArgumentException(
                    "Invalid request status");
        }

        BulkPickupRequest request =
                bulkPickupRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Request not found"));

        String currentStatus = request.getRequestStatus();

        boolean allowed = false;

        if ("PENDING".equals(currentStatus)) {

            allowed = status.equals("APPROVED") ||
                    status.equals("REJECTED");

        } else if ("APPROVED".equals(currentStatus)) {

            allowed = status.equals("IN_PROGRESS");

        } else if ("IN_PROGRESS".equals(currentStatus)) {

            allowed = status.equals("COMPLETED");
        }

        if (!allowed) {
            throw new IllegalArgumentException(
                    "Cannot change status from " +
                    currentStatus + " to " + status);
        }

        request.setRequestStatus(status);

        BulkPickupRequest saved =
                bulkPickupRequestRepository.save(request);

        return toAdminResponse(saved);
    }
}
