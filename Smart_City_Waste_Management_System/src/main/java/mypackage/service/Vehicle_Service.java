
package mypackage.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.model.Vehicle;
import mypackage.repository.Vehicle_Repository;

@Service
public class Vehicle_Service {

    @Autowired
    private Vehicle_Repository vehicleRepository;

    // ==========================================
    // Validate Vehicle Information
    // ==========================================

    private void validateVehicle(Vehicle vehicle) {

        if (vehicle == null) {
            throw new IllegalArgumentException(
                "Vehicle details are required.");
        }

        if (vehicle.getVehicleNumber() == null ||
            vehicle.getVehicleNumber().trim().isEmpty()) {

            throw new IllegalArgumentException(
                "Vehicle number is required.");
        }

        if (vehicle.getVehicleType() == null ||
            vehicle.getVehicleType().trim().isEmpty()) {

            throw new IllegalArgumentException(
                "Vehicle type is required.");
        }

        if (vehicle.getCapacityKg() == null ||
            vehicle.getCapacityKg().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                "Vehicle capacity must be greater than zero.");
        }

        if (vehicle.getVehicleNumber().trim().length() > 30) {
            throw new IllegalArgumentException(
                "Vehicle number cannot exceed 30 characters.");
        }

        if (vehicle.getVehicleType().trim().length() > 50) {
            throw new IllegalArgumentException(
                "Vehicle type cannot exceed 50 characters.");
        }

        if (vehicle.getCapacityKg().scale() > 2 ||
            vehicle.getCapacityKg().precision()
                - vehicle.getCapacityKg().scale() > 8) {

            throw new IllegalArgumentException(
                "Capacity must fit DECIMAL(10,2).");
        }

        String status = vehicle.getVehicleStatus();

        if (status == null || status.trim().isEmpty()) {
            status = "AVAILABLE";
        }

        status = status.trim().toUpperCase(Locale.ROOT);

        if (!status.equals("AVAILABLE") &&
            !status.equals("IN_USE") &&
            !status.equals("MAINTENANCE")) {

            throw new IllegalArgumentException(
                "Vehicle status must be AVAILABLE, IN_USE or MAINTENANCE.");
        }

        // Normalize values before saving.
        vehicle.setVehicleNumber(
            vehicle.getVehicleNumber()
                .trim()
                .toUpperCase(Locale.ROOT)
        );

        vehicle.setVehicleType(
            vehicle.getVehicleType().trim()
        );

        vehicle.setVehicleStatus(status);
    }

    // ==========================================
    // Check Duplicate Vehicle Number
    // ==========================================

    private void checkDuplicateVehicleNumber(
            String vehicleNumber,
            Long currentVehicleId) {

        Optional<Vehicle> existing =
            vehicleRepository.findByVehicleNumberIgnoreCase(
                vehicleNumber);

        if (existing.isPresent()) {

            if (currentVehicleId == null ||
                !existing.get().getVehicleId()
                    .equals(currentVehicleId)) {

                throw new IllegalArgumentException(
                    "Vehicle number already exists.");
            }
        }
    }

    // ==========================================
    // Add New Vehicle
    // ==========================================

    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle) {

        validateVehicle(vehicle);

        checkDuplicateVehicleNumber(
            vehicle.getVehicleNumber(),
            null
        );

        // Create a new object so clients cannot
        // insert an ID or modify relationships.
        Vehicle newVehicle = new Vehicle();

        newVehicle.setVehicleNumber(
            vehicle.getVehicleNumber());

        newVehicle.setVehicleType(
            vehicle.getVehicleType());

        newVehicle.setCapacityKg(
            vehicle.getCapacityKg());

        newVehicle.setVehicleStatus(
            vehicle.getVehicleStatus());

        return vehicleRepository.save(newVehicle);
    }

    // ==========================================
    // Get All Vehicles
    // ==========================================

    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // ==========================================
    // Get Vehicle By ID
    // ==========================================

    @Transactional(readOnly = true)
    public Optional<Vehicle> getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    // ==========================================
    // Update Vehicle
    // ==========================================

    @Transactional
    public Optional<Vehicle> updateVehicle(
            Long vehicleId,
            Vehicle updatedVehicle) {

        Optional<Vehicle> existingOptional =
            vehicleRepository.findById(vehicleId);

        if (!existingOptional.isPresent()) {
            return Optional.empty();
        }

        validateVehicle(updatedVehicle);

        checkDuplicateVehicleNumber(
            updatedVehicle.getVehicleNumber(),
            vehicleId
        );

        Vehicle existingVehicle = existingOptional.get();

        existingVehicle.setVehicleNumber(
            updatedVehicle.getVehicleNumber());

        existingVehicle.setVehicleType(
            updatedVehicle.getVehicleType());

        existingVehicle.setCapacityKg(
            updatedVehicle.getCapacityKg());

        existingVehicle.setVehicleStatus(
            updatedVehicle.getVehicleStatus());

        return Optional.of(
            vehicleRepository.save(existingVehicle)
        );
    }
}
