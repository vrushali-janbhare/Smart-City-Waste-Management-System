
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Collection_Records;
import mypackage.repository.Collection_Records_Repository;

@Service
public class Collection_Records_Service {

    @Autowired
    private Collection_Records_Repository collectionRecordsRepository;

    // Add Collection Record
    public Collection_Records addRecord(Collection_Records record) {
        return collectionRecordsRepository.save(record);
    }

    // Get Record By ID
    public Collection_Records getRecordById(Long id) {
        return collectionRecordsRepository.findById(id).orElse(null);
    }

    // Get All Records
    public List<Collection_Records> getAllRecords() {
        return collectionRecordsRepository.findAll();
    }

    // Update Record
    public Collection_Records updateRecord(Long id, Collection_Records record) {

        Collection_Records existingRecord =
                collectionRecordsRepository.findById(id).orElse(null);

        if (existingRecord != null) {

            existingRecord.setCollection_date(record.getCollection_date());
            existingRecord.setCollected_quantity(record.getCollected_quantity());
            existingRecord.setRemarks(record.getRemarks());
            existingRecord.setCollection_schedule(record.getCollection_schedule());
            existingRecord.setWorkers(record.getWorkers());

            return collectionRecordsRepository.save(existingRecord);
        }

        return null;
    }

    // Delete Record
    public String deleteRecord(Long id) {

        collectionRecordsRepository.deleteById(id);

        return "Collection Record Deleted Successfully";
    }
}
