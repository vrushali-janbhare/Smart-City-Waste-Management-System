package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Vehicles;
import mypackage.service.Vehicles_Service;

@RestController
@RequestMapping("/api/vehicles")
public class Vehicles_Controller {

    @Autowired
    private Vehicles_Service vehiclesService;

    // Add Vehicle
    @PostMapping
    public Vehicles addVehicle(@RequestBody Vehicles vehicle) {
        return vehiclesService.addVehicle(vehicle);
    }

    // Get All Vehicles
    @GetMapping
    public List<Vehicles> getAllVehicles() {
        return vehiclesService.getAllVehicles();
    }

    // Get Vehicle By ID
    @GetMapping("/{id}")
    public Vehicles getVehicleById(@PathVariable Long id) {
        return vehiclesService.getVehicleById(id);
    }

    // Update Vehicle
    @PutMapping("/{id}")
    public Vehicles updateVehicle(
            @PathVariable Long id,
            @RequestBody Vehicles vehicle) {

        return vehiclesService.updateVehicle(id, vehicle);
    }

    // Delete Vehicle
    @DeleteMapping("/{id}")
    public String deleteVehicle(@PathVariable Long id) {

        vehiclesService.deleteVehicle(id);

        return "Vehicle Deleted Successfully!";
    }
}