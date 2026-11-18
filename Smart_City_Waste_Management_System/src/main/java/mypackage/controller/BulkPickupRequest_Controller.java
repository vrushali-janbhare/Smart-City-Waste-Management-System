
package mypackage.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.BulkPickupRequest;
import mypackage.service.BulkPickupRequest_Service;

@RestController
@RequestMapping("/api/bulk-pickup-requests")
public class BulkPickupRequest_Controller {

    @Autowired
    private BulkPickupRequest_Service bulkPickupRequestService;

    // Citizen form DTO
    public static class BulkPickupRequestDTO {

        private Long areaId;
        private Long categoryId;
        private LocalDate requestedDate;
        private String pickupAddress;
        private BigDecimal estimatedQuantityKg;

        public Long getAreaId() {
            return areaId;
        }

        public void setAreaId(Long areaId) {
            this.areaId = areaId;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public LocalDate getRequestedDate() {
            return requestedDate;
        }

        public void setRequestedDate(LocalDate requestedDate) {
            this.requestedDate = requestedDate;
        }

        public String getPickupAddress() {
            return pickupAddress;
        }

        public void setPickupAddress(String pickupAddress) {
            this.pickupAddress = pickupAddress;
        }

        public BigDecimal getEstimatedQuantityKg() {
            return estimatedQuantityKg;
        }

        public void setEstimatedQuantityKg(
                BigDecimal estimatedQuantityKg) {
            this.estimatedQuantityKg = estimatedQuantityKg;
        }
    }

    // Admin status update DTO
    public static class StatusUpdateDTO {

        private String requestStatus;

        public String getRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(String requestStatus) {
            this.requestStatus = requestStatus;
        }
    }

    private Long getCitizenId(HttpSession session) {

        Object userId =
                session.getAttribute("loggedInUserId");

        String role = (String) session.getAttribute(
                "loggedInUserRole");

        if (!(userId instanceof Long) ||
                !"USER".equals(role)) {
            return null;
        }

        return (Long) userId;
    }

    private boolean isAdmin(HttpSession session) {

        Object userId =
                session.getAttribute("loggedInUserId");

        String role = (String) session.getAttribute(
                "loggedInUserRole");

        return userId instanceof Long &&
                "ADMIN".equals(role);
    }

    private Map<String, String> message(String text) {
        return Collections.singletonMap("message", text);
    }

    private Map<String, Object> toResponse(
            BulkPickupRequest request) {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("requestId", request.getRequestId());
        result.put("requestedDate", request.getRequestedDate());
        result.put("pickupAddress", request.getPickupAddress());

        result.put("estimatedQuantityKg",
                request.getEstimatedQuantityKg());

        result.put("requestStatus",
                request.getRequestStatus());

        result.put("areaId",
                request.getArea().getAreaId());

        result.put("categoryId",
                request.getCategory().getCategoryId());

        return result;
    }

    // =====================================
    // CITIZEN ENDPOINTS
    // =====================================

    // 1. Submit a new request
    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestBody BulkPickupRequestDTO data,
            HttpSession session) {

        Long userId = getCitizenId(session);

        if (userId == null) {
            return ResponseEntity.status(403).body(
                    message("Citizen login is required"));
        }

        try {

            BulkPickupRequest saved =
                    bulkPickupRequestService.createRequest(
                            userId,
                            data.getAreaId(),
                            data.getCategoryId(),
                            data.getRequestedDate(),
                            data.getPickupAddress(),
                            data.getEstimatedQuantityKg());

            return ResponseEntity.ok(toResponse(saved));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    message(e.getMessage()));
        }
    }

    // 2. View logged-in citizen's requests
    @GetMapping("/my-requests")
    public ResponseEntity<?> getMyRequests(
            HttpSession session) {

        Long userId = getCitizenId(session);

        if (userId == null) {
            return ResponseEntity.status(403).body(
                    message("Citizen login is required"));
        }

        List<BulkPickupRequest> requests =
                bulkPickupRequestService.getMyRequests(userId);

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (BulkPickupRequest request : requests) {
            result.add(toResponse(request));
        }

        return ResponseEntity.ok(result);
    }

    // 3. View own request by ID
    @GetMapping("/{requestId:\\d+}")
    public ResponseEntity<?> getMyRequestById(
            @PathVariable Long requestId,
            HttpSession session) {

        Long userId = getCitizenId(session);

        if (userId == null) {
            return ResponseEntity.status(403).body(
                    message("Citizen login is required"));
        }

        return bulkPickupRequestService
                .getMyRequestById(requestId, userId)
                .<ResponseEntity<?>>map(request ->
                        ResponseEntity.ok(toResponse(request)))
                .orElse(ResponseEntity.notFound().build());
    }

    // =====================================
    // ADMIN ENDPOINTS
    // =====================================

    // 4. Admin: View all requests
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllRequestsForAdmin(
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(
                    message("Admin access is required"));
        }

        return ResponseEntity.ok(
                bulkPickupRequestService
                        .getAllRequestsForAdmin());
    }

    // 5. Admin: View request details
    @GetMapping("/admin/{requestId:\\d+}")
    public ResponseEntity<?> getRequestForAdmin(
            @PathVariable Long requestId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(
                    message("Admin access is required"));
        }

        return bulkPickupRequestService
                .getRequestForAdmin(requestId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. Admin: Update request status
    @PatchMapping("/admin/{requestId:\\d+}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestBody StatusUpdateDTO data,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(
                    message("Admin access is required"));
        }

        try {

            return ResponseEntity.ok(
                    bulkPickupRequestService.updateRequestStatus(
                            requestId,
                            data.getRequestStatus()));

        } catch (IllegalArgumentException e) {

            if ("Request not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.badRequest().body(
                    message(e.getMessage()));
        }
    }
}
