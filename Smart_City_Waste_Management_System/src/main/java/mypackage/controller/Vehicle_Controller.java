
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Vehicle;
import mypackage.service.Vehicle_Service;

@RestController
@RequestMapping("/api/vehicles")
public class Vehicle_Controller {

    @Autowired
    private Vehicle_Service vehicleService;

    // Insert new vehicle
    @PostMapping
    public Vehicle saveVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.saveVehicle(vehicle);
    }

    // Get all vehicles
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    // Get vehicle by ID
    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(
            @PathVariable Long vehicleId) {

        return vehicleService.getVehicleById(vehicleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update vehicle by ID
    @PutMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody Vehicle vehicle) {

        return vehicleService.getVehicleById(vehicleId)
                .map(existingVehicle -> {
                    vehicle.setVehicleId(vehicleId);
                    return ResponseEntity.ok(
                            vehicleService.saveVehicle(vehicle));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete vehicle by ID
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long vehicleId) {

        return vehicleService.getVehicleById(vehicleId)
                .map(existingVehicle -> {
                    vehicleService.deleteVehicle(vehicleId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
