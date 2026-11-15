
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.CollectionScheduleDay;
import mypackage.repository.CollectionScheduleDay_Repository;

@Service
public class CollectionScheduleDay_Service {

    @Autowired
    private CollectionScheduleDay_Repository collectionScheduleDayRepository;

    // Insert or update schedule day
    public CollectionScheduleDay saveCollectionScheduleDay(CollectionScheduleDay scheduleDay) {
        return collectionScheduleDayRepository.save(scheduleDay);
    }

    // Get all schedule days
    public List<CollectionScheduleDay> getAllCollectionScheduleDays() {
        return collectionScheduleDayRepository.findAll();
    }

    // Get schedule day by ID
    public Optional<CollectionScheduleDay> getCollectionScheduleDayById(Long scheduleDayId) {
        return collectionScheduleDayRepository.findById(scheduleDayId);
    }

    // Delete schedule day by ID
    public void deleteCollectionScheduleDay(Long scheduleDayId) {
        collectionScheduleDayRepository.deleteById(scheduleDayId);
    }
}
