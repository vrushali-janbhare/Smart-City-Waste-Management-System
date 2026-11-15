
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.CollectionRecord;
import mypackage.repository.CollectionRecord_Repository;

@Service
public class CollectionRecord_Service {

    @Autowired
    private CollectionRecord_Repository collectionRecordRepository;

    // Insert or update collection record
    public CollectionRecord saveCollectionRecord(CollectionRecord record) {
        return collectionRecordRepository.save(record);
    }

    // Get all collection records
    public List<CollectionRecord> getAllCollectionRecords() {
        return collectionRecordRepository.findAll();
    }

    // Get collection record by ID
    public Optional<CollectionRecord> getCollectionRecordById(Long collectionRecordId) {
        return collectionRecordRepository.findById(collectionRecordId);
    }

    // Delete collection record by ID
    public void deleteCollectionRecord(Long collectionRecordId) {
        collectionRecordRepository.deleteById(collectionRecordId);
    }
}
