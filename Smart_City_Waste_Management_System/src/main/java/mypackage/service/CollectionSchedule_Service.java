
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.CollectionSchedule;
import mypackage.repository.CollectionSchedule_Repository;

@Service
public class CollectionSchedule_Service {

    @Autowired
    private CollectionSchedule_Repository collectionScheduleRepository;

    // Insert or update collection schedule
    public CollectionSchedule saveCollectionSchedule(CollectionSchedule schedule) {
        return collectionScheduleRepository.save(schedule);
    }

    // Get all collection schedules
    public List<CollectionSchedule> getAllCollectionSchedules() {
        return collectionScheduleRepository.findAll();
    }

    // Get collection schedule by ID
    public Optional<CollectionSchedule> getCollectionScheduleById(Long scheduleId) {
        return collectionScheduleRepository.findById(scheduleId);
    }

    // Delete collection schedule by ID
    public void deleteCollectionSchedule(Long scheduleId) {
        collectionScheduleRepository.deleteById(scheduleId);
    }
}
