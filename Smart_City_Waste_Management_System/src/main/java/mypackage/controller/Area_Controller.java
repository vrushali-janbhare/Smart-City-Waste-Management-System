
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Area;
import mypackage.service.Area_Service;

@RestController
@RequestMapping("/api/areas")
public class Area_Controller {

    @Autowired
    private Area_Service areaService;

    // Insert new area
    @PostMapping
    public Area saveArea(@RequestBody Area area) {
        return areaService.saveArea(area);
    }

    // Get all areas
    @GetMapping
    public List<Area> getAllAreas() {
        return areaService.getAllAreas();
    }

    // Get area by ID
    @GetMapping("/{areaId}")
    public ResponseEntity<Area> getAreaById(
            @PathVariable Long areaId) {

        return areaService.getAreaById(areaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update area by ID
    @PutMapping("/{areaId}")
    public ResponseEntity<Area> updateArea(
            @PathVariable Long areaId,
            @RequestBody Area area) {

        return areaService.getAreaById(areaId)
                .map(existingArea -> {
                    area.setAreaId(areaId);
                    return ResponseEntity.ok(
                            areaService.saveArea(area));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete area by ID
    @DeleteMapping("/{areaId}")
    public ResponseEntity<Void> deleteArea(
            @PathVariable Long areaId) {

        return areaService.getAreaById(areaId)
                .map(existingArea -> {
                    areaService.deleteArea(areaId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
