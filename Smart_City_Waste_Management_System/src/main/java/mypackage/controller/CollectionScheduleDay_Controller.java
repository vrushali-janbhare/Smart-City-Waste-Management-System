
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.CollectionScheduleDay;
import mypackage.service.CollectionScheduleDay_Service;

//@RestController
//@RequestMapping("/api/collection-schedule-days")
public class CollectionScheduleDay_Controller {

    @Autowired
    private CollectionScheduleDay_Service collectionScheduleDayService;

    // Insert new collection schedule day
    @PostMapping
    public CollectionScheduleDay saveCollectionScheduleDay(
            @RequestBody CollectionScheduleDay collectionScheduleDay) {

        return collectionScheduleDayService.saveCollectionScheduleDay(
                collectionScheduleDay);
    }

    // Get all collection schedule days
    @GetMapping
    public List<CollectionScheduleDay> getAllCollectionScheduleDays() {
        return collectionScheduleDayService.getAllCollectionScheduleDays();
    }

    // Get collection schedule day by ID
    @GetMapping("/{scheduleDayId}")
    public ResponseEntity<CollectionScheduleDay> getCollectionScheduleDayById(
            @PathVariable Long scheduleDayId) {

        return collectionScheduleDayService.getCollectionScheduleDayById(scheduleDayId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update collection schedule day by ID
    @PutMapping("/{scheduleDayId}")
    public ResponseEntity<CollectionScheduleDay> updateCollectionScheduleDay(
            @PathVariable Long scheduleDayId,
            @RequestBody CollectionScheduleDay collectionScheduleDay) {

        return collectionScheduleDayService.getCollectionScheduleDayById(scheduleDayId)
                .map(existingDay -> {
                    collectionScheduleDay.setScheduleDayId(scheduleDayId);
                    return ResponseEntity.ok(
                            collectionScheduleDayService.saveCollectionScheduleDay(
                                    collectionScheduleDay));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete collection schedule day by ID
    @DeleteMapping("/{scheduleDayId}")
    public ResponseEntity<Void> deleteCollectionScheduleDay(
            @PathVariable Long scheduleDayId) {

        return collectionScheduleDayService.getCollectionScheduleDayById(scheduleDayId)
                .map(existingDay -> {
                    collectionScheduleDayService.deleteCollectionScheduleDay(scheduleDayId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
