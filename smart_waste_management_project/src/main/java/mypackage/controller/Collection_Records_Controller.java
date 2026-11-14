
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Collection_Records;
import mypackage.service.Collection_Records_Service;

@RestController
@RequestMapping("/api/collection-records")
public class Collection_Records_Controller {

    @Autowired
    private Collection_Records_Service collectionRecordsService;

    // Add Record
    @PostMapping
    public Collection_Records addRecord(
            @RequestBody Collection_Records record) {

        return collectionRecordsService.addRecord(record);
    }

    // Get Record By ID
    @GetMapping("/{id}")
    public Collection_Records getRecordById(
            @PathVariable Long id) {

        return collectionRecordsService.getRecordById(id);
    }

    // Get All Records
    @GetMapping
    public List<Collection_Records> getAllRecords() {

        return collectionRecordsService.getAllRecords();
    }

    // Update Record
    @PutMapping("/{id}")
    public Collection_Records updateRecord(
            @PathVariable Long id,
            @RequestBody Collection_Records record) {

        return collectionRecordsService.updateRecord(id, record);
    }

    // Delete Record
    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable Long id) {

        return collectionRecordsService.deleteRecord(id);
    }
}
