package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Vehicles;
import mypackage.repository.Vehicles_Repository;

@Service
public class Vehicles_Service {

    @Autowired
    private Vehicles_Repository vehiclesRepository;

    // Add Vehicle
    public Vehicles addVehicle(Vehicles vehicle) {
        return vehiclesRepository.save(vehicle);
    }

    // Get All Vehicles
    public List<Vehicles> getAllVehicles() {
        return vehiclesRepository.findAll();
    }

    // Get Vehicle By ID
    public Vehicles getVehicleById(Long id) {
        return vehiclesRepository.findById(id).orElse(null);
    }

    // Update Vehicle
    public Vehicles updateVehicle(Long id, Vehicles vehicle) {

        Vehicles existingVehicle =
                vehiclesRepository.findById(id).orElse(null);

        if (existingVehicle != null) {

            existingVehicle.setVehicle_number(vehicle.getVehicle_number());
            existingVehicle.setVehicle_type(vehicle.getVehicle_type());
            existingVehicle.setCapacity(vehicle.getCapacity());
            existingVehicle.setStatus(vehicle.getStatus());

            return vehiclesRepository.save(existingVehicle);
        }

        return null;
    }

    // Delete Vehicle
    public void deleteVehicle(Long id) {
        vehiclesRepository.deleteById(id);
    }
}