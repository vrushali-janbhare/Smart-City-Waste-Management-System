package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Fines;
import mypackage.service.Fines_Service;

@RestController
@RequestMapping("/api/fines")
public class Fines_Controller {

    @Autowired
    private Fines_Service finesService;

    // Add Fine
    @PostMapping
    public Fines addFine(@RequestBody Fines fine) {
        return finesService.addFine(fine);
    }

    // Get All Fines
    @GetMapping
    public List<Fines> getAllFines() {
        return finesService.getAllFines();
    }

    // Get Fine By ID
    @GetMapping("/{id}")
    public Fines getFineById(@PathVariable Long id) {
        return finesService.getFineById(id);
    }

    // Update Fine
    @PutMapping("/{id}")
    public Fines updateFine(
            @PathVariable Long id,
            @RequestBody Fines fine) {

        return finesService.updateFine(id, fine);
    }

    // Delete Fine
    @DeleteMapping("/{id}")
    public String deleteFine(@PathVariable Long id) {

        finesService.deleteFine(id);

        return "Fine Deleted Successfully!";
    }
}