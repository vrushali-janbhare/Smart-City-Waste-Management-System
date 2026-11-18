
package mypackage.controller;

import java.util.List;
import java.util.Map;
import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.WasteCategory;
import mypackage.service.WasteCategory_Service;

@RestController
@RequestMapping("/api/waste-categories")
public class WasteCategory_Controller {

    @Autowired
    private WasteCategory_Service wasteCategoryService;

    // ==========================================
    // 1. CHECK ADMIN LOGIN
    // ==========================================

    private boolean isAdmin(HttpSession session) {

        Object userId =
                session.getAttribute("loggedInUserId");

        Object role =
                session.getAttribute("loggedInUserRole");

        return userId != null &&
                "ADMIN".equals(String.valueOf(role));
    }

    // ==========================================
    // 2. ADD NEW WASTE CATEGORY
    // ADMIN ONLY
    // ==========================================

    @PostMapping
    public ResponseEntity<?> saveWasteCategory(
            @RequestBody WasteCategory wasteCategory,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(error("Access denied. Admin only."));
        }

        try {

            // New category must not reuse an existing ID.
            wasteCategory.setCategoryId(null);

            WasteCategory savedCategory =
                    wasteCategoryService
                            .saveWasteCategory(wasteCategory);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedCategory);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(error(e.getMessage()));
        }
    }

    // ==========================================
    // 3. GET ALL WASTE CATEGORIES
    // ==========================================

    @GetMapping
    public ResponseEntity<List<WasteCategory>>
            getAllWasteCategories() {

        List<WasteCategory> categories =
                wasteCategoryService
                        .getAllWasteCategories();

        return ResponseEntity.ok(categories);
    }

    // ==========================================
    // 4. GET WASTE CATEGORY BY ID
    // ==========================================

    @GetMapping("/{categoryId}")
    public ResponseEntity<WasteCategory>
            getWasteCategoryById(
                    @PathVariable Long categoryId) {

        return wasteCategoryService
                .getWasteCategoryById(categoryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 5. UPDATE WASTE CATEGORY
    // ADMIN ONLY
    // ==========================================

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateWasteCategory(
            @PathVariable Long categoryId,
            @RequestBody WasteCategory wasteCategory,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(error("Access denied. Admin only."));
        }

        if (!wasteCategoryService
                .getWasteCategoryById(categoryId)
                .isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error("Waste category not found."));
        }

        try {

            WasteCategory updatedCategory =
                    wasteCategoryService
                            .updateWasteCategory(
                                    categoryId,
                                    wasteCategory
                            );

            return ResponseEntity.ok(updatedCategory);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(error(e.getMessage()));
        }
    }

    // ==========================================
    // 6. DELETE WASTE CATEGORY
    // ADMIN ONLY - NOT USED IN FRONTEND
    // ==========================================

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteWasteCategory(
            @PathVariable Long categoryId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(error("Access denied. Admin only."));
        }

        if (!wasteCategoryService
                .getWasteCategoryById(categoryId)
                .isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error("Waste category not found."));
        }

        try {

            wasteCategoryService
                    .deleteWasteCategory(categoryId);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(error(
                        "Cannot delete this category. " +
                        "It may be used by existing records."
                    ));
        }
    }

    // ==========================================
    // 7. ERROR RESPONSE HELPER
    // ==========================================

    private Map<String, String> error(String message) {

        return Collections.singletonMap(
                "message",
                message
        );
    }
}
