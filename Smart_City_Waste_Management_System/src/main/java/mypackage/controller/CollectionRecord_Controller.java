
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.CollectionRecord;
import mypackage.service.CollectionRecord_Service;
//
//@RestController
//@RequestMapping("/api/collection-records")
public class CollectionRecord_Controller {

    @Autowired
    private CollectionRecord_Service collectionRecordService;

    // Insert new collection record
    @PostMapping
    public CollectionRecord saveCollectionRecord(
            @RequestBody CollectionRecord collectionRecord) {

        return collectionRecordService.saveCollectionRecord(collectionRecord);
    }

    // Get all collection records
    @GetMapping
    public List<CollectionRecord> getAllCollectionRecords() {

        return collectionRecordService.getAllCollectionRecords();
    }

    // Get collection record by ID
    @GetMapping("/{collectionRecordId}")
    public ResponseEntity<CollectionRecord> getCollectionRecordById(
            @PathVariable Long collectionRecordId) {

        return collectionRecordService
                .getCollectionRecordById(collectionRecordId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update collection record by ID
    @PutMapping("/{collectionRecordId}")
    public ResponseEntity<CollectionRecord> updateCollectionRecord(
            @PathVariable Long collectionRecordId,
            @RequestBody CollectionRecord collectionRecord) {

        return collectionRecordService
                .getCollectionRecordById(collectionRecordId)
                .map(existingRecord -> {

                    collectionRecord.setCollectionRecordId(collectionRecordId);

                    // Preserve existing collection time
                    if (collectionRecord.getCollectionTime() == null) {
                        collectionRecord.setCollectionTime(
                                existingRecord.getCollectionTime());
                    }

                    return ResponseEntity.ok(
                            collectionRecordService.saveCollectionRecord(
                                    collectionRecord));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete collection record by ID
    @DeleteMapping("/{collectionRecordId}")
    public ResponseEntity<Void> deleteCollectionRecord(
            @PathVariable Long collectionRecordId) {

        return collectionRecordService
                .getCollectionRecordById(collectionRecordId)
                .map(existingRecord -> {

                    collectionRecordService
                            .deleteCollectionRecord(collectionRecordId);

                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
