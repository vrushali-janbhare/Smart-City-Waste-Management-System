
package mypackage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Vehicle;
import mypackage.service.Vehicle_Service;

@RestController
@RequestMapping("/api/vehicles")
public class Vehicle_Controller {

    @Autowired
    private Vehicle_Service vehicleService;

    // ==========================================
    // Check Admin Session
    // ==========================================

    private boolean isAdmin(HttpSession session) {

        Object userId =
            session.getAttribute("loggedInUserId");

        Object role =
            session.getAttribute("loggedInUserRole");

        return userId != null &&
               role != null &&
               "ADMIN".equalsIgnoreCase(role.toString());
    }

    // ==========================================
    // Error Response
    // ==========================================

    private Map<String, String> errorMessage(String message) {

        Map<String, String> error = new HashMap<>();
        error.put("message", message);

        return error;
    }

    // ==========================================
    // POST - Add Vehicle
    // ==========================================

    @PostMapping
    public ResponseEntity<?> saveVehicle(
            @RequestBody Vehicle vehicle,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorMessage("Admin access required."));
        }

        try {

            Vehicle savedVehicle =
                vehicleService.saveVehicle(vehicle);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedVehicle);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                .badRequest()
                .body(errorMessage(e.getMessage()));
        }
    }

    // ==========================================
    // GET - All Vehicles
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {

        return ResponseEntity.ok(
            vehicleService.getAllVehicles()
        );
    }

    // ==========================================
    // GET - Vehicle By ID
    // ==========================================

    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(
            @PathVariable Long vehicleId) {

        return vehicleService.getVehicleById(vehicleId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // PUT - Update Vehicle
    // ==========================================

    @PutMapping("/{vehicleId}")
    public ResponseEntity<?> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody Vehicle vehicle,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorMessage("Admin access required."));
        }

        try {

            Optional<Vehicle> updatedVehicle =
                vehicleService.updateVehicle(vehicleId, vehicle);

            if (!updatedVehicle.isPresent()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage("Vehicle not found."));
            }

            return ResponseEntity.ok(updatedVehicle.get());

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                .badRequest()
                .body(errorMessage(e.getMessage()));
        }
    }

    // ==========================================
    // DELETE - Disabled
    // ==========================================

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<?> deleteVehicle(
            @PathVariable Long vehicleId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorMessage("Admin access required."));
        }

        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(errorMessage(
                "Vehicle deletion is disabled to preserve collection records."));
    }
}
