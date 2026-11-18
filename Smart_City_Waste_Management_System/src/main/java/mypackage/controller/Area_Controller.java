
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

import mypackage.model.Area;
import mypackage.service.Area_Service;

@RestController
@RequestMapping("/api/areas")
public class Area_Controller {

    @Autowired
    private Area_Service areaService;

    // Check admin session
    private boolean isAdmin(HttpSession session) {

        Object userId =
                session.getAttribute("loggedInUserId");

        Object role =
                session.getAttribute("loggedInUserRole");

        return userId != null
                && role != null
                && "ADMIN".equalsIgnoreCase(role.toString());
    }

    // Error response
    private Map<String, String> errorMessage(String message) {

        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return response;
    }

    // Add new area - Admin only
    @PostMapping
    public ResponseEntity<?> saveArea(
            @RequestBody Area area,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(errorMessage("Admin access required."));
        }

        try {

            Area savedArea = areaService.saveArea(area);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedArea);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(errorMessage(e.getMessage()));
        }
    }

    // Get all areas
    @GetMapping
    public ResponseEntity<List<Area>> getAllAreas() {

        return ResponseEntity.ok(areaService.getAllAreas());
    }

    // Get area by ID
    @GetMapping("/{areaId}")
    public ResponseEntity<Area> getAreaById(
            @PathVariable Long areaId) {

        return areaService.getAreaById(areaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update area - Admin only
    @PutMapping("/{areaId}")
    public ResponseEntity<?> updateArea(
            @PathVariable Long areaId,
            @RequestBody Area area,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(errorMessage("Admin access required."));
        }

        try {

            Optional<Area> updated =
                    areaService.updateArea(areaId, area);

            if (!updated.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(errorMessage("Area not found."));
            }

            return ResponseEntity.ok(updated.get());

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(errorMessage(e.getMessage()));
        }
    }

    // Delete area - Admin only
    // Disabled to protect related records.
    @DeleteMapping("/{areaId}")
    public ResponseEntity<?> deleteArea(
            @PathVariable Long areaId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(errorMessage("Admin access required."));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorMessage(
                        "Deleting areas is not allowed."));
    }
}
