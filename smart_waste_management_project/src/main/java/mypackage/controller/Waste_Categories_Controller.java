
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Waste_Categories;
import mypackage.service.Waste_Categories_Service;

@RestController
@RequestMapping("/api/waste-categories")
public class Waste_Categories_Controller {

    @Autowired
    private Waste_Categories_Service wasteCategoriesService;

    // Add Waste Category
    @PostMapping
    public Waste_Categories addWasteCategory(@RequestBody Waste_Categories category) {
        return wasteCategoriesService.addWasteCategory(category);
    }

    // Get All Waste Categories
    @GetMapping
    public List<Waste_Categories> getAllWasteCategories() {
        return wasteCategoriesService.getAllWasteCategories();
    }

    // Get Waste Category By ID
    @GetMapping("/{id}")
    public Waste_Categories getWasteCategoryById(@PathVariable Long id) {
        return wasteCategoriesService.getWasteCategoryById(id);
    }

    // Update Waste Category
    @PutMapping("/{id}")
    public Waste_Categories updateWasteCategory(
            @PathVariable Long id,
            @RequestBody Waste_Categories category) {

        return wasteCategoriesService.updateWasteCategory(id, category);
    }

    // Delete Waste Category
    @DeleteMapping("/{id}")
    public String deleteWasteCategory(@PathVariable Long id) {
        wasteCategoriesService.deleteWasteCategory(id);
        return "Waste Category Deleted Successfully!";
    }
}
