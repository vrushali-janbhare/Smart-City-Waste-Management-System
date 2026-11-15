
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.CollectionSchedule;
import mypackage.service.CollectionSchedule_Service;

@RestController
@RequestMapping("/api/collection-schedules")
public class CollectionSchedule_Controller {

    @Autowired
    private CollectionSchedule_Service collectionScheduleService;

    // Insert new collection schedule
    @PostMapping
    public CollectionSchedule saveCollectionSchedule(
            @RequestBody CollectionSchedule collectionSchedule) {

        return collectionScheduleService.saveCollectionSchedule(collectionSchedule);
    }

    // Get all collection schedules
    @GetMapping
    public List<CollectionSchedule> getAllCollectionSchedules() {
        return collectionScheduleService.getAllCollectionSchedules();
    }

    // Get collection schedule by ID
    @GetMapping("/{scheduleId}")
    public ResponseEntity<CollectionSchedule> getCollectionScheduleById(
            @PathVariable Long scheduleId) {

        return collectionScheduleService.getCollectionScheduleById(scheduleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update collection schedule by ID
    @PutMapping("/{scheduleId}")
    public ResponseEntity<CollectionSchedule> updateCollectionSchedule(
            @PathVariable Long scheduleId,
            @RequestBody CollectionSchedule collectionSchedule) {

        return collectionScheduleService.getCollectionScheduleById(scheduleId)
                .map(existingSchedule -> {
                    collectionSchedule.setScheduleId(scheduleId);
                    return ResponseEntity.ok(
                            collectionScheduleService.saveCollectionSchedule(collectionSchedule));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete collection schedule by ID
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteCollectionSchedule(
            @PathVariable Long scheduleId) {

        return collectionScheduleService.getCollectionScheduleById(scheduleId)
                .map(existingSchedule -> {
                    collectionScheduleService.deleteCollectionSchedule(scheduleId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
