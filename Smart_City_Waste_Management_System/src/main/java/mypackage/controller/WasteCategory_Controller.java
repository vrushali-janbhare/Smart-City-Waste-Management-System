
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.WasteCategory;
import mypackage.service.WasteCategory_Service;

@RestController
@RequestMapping("/api/waste-categories")
public class WasteCategory_Controller {

    @Autowired
    private WasteCategory_Service wasteCategoryService;

    // Insert new waste category
    @PostMapping
    public WasteCategory saveWasteCategory(
            @RequestBody WasteCategory wasteCategory) {

        return wasteCategoryService.saveWasteCategory(wasteCategory);
    }

    // Get all waste categories
    @GetMapping
    public List<WasteCategory> getAllWasteCategories() {
        return wasteCategoryService.getAllWasteCategories();
    }

    // Get waste category by ID
    @GetMapping("/{categoryId}")
    public ResponseEntity<WasteCategory> getWasteCategoryById(
            @PathVariable Long categoryId) {

        return wasteCategoryService.getWasteCategoryById(categoryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update waste category by ID
    @PutMapping("/{categoryId}")
    public ResponseEntity<WasteCategory> updateWasteCategory(
            @PathVariable Long categoryId,
            @RequestBody WasteCategory wasteCategory) {

        return wasteCategoryService.getWasteCategoryById(categoryId)
                .map(existingCategory -> {
                    wasteCategory.setCategoryId(categoryId);
                    return ResponseEntity.ok(
                            wasteCategoryService.saveWasteCategory(wasteCategory));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete waste category by ID
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteWasteCategory(
            @PathVariable Long categoryId) {

        return wasteCategoryService.getWasteCategoryById(categoryId)
                .map(existingCategory -> {
                    wasteCategoryService.deleteWasteCategory(categoryId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
