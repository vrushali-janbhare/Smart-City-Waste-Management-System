
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Collection_Schedule_Days;
import mypackage.repository.Collection_Schedule_Days_Repository;

@Service
public class Collection_Schedule_Days_Service {

    @Autowired
    private Collection_Schedule_Days_Repository scheduleDaysRepository;

    // Add Schedule Day
    public Collection_Schedule_Days addScheduleDay(Collection_Schedule_Days day) {
        return scheduleDaysRepository.save(day);
    }

    // Get Schedule Day By ID
    public Collection_Schedule_Days getScheduleDayById(Long id) {
        return scheduleDaysRepository.findById(id).orElse(null);
    }

    // Get All Schedule Days
    public List<Collection_Schedule_Days> getAllScheduleDays() {
        return scheduleDaysRepository.findAll();
    }

    // Update Schedule Day
    public Collection_Schedule_Days updateScheduleDay(
            Long id, Collection_Schedule_Days day) {

        Collection_Schedule_Days existingDay =
                scheduleDaysRepository.findById(id).orElse(null);

        if (existingDay != null) {

            existingDay.setCollection_schedule(day.getCollection_schedule());
            existingDay.setDay_of_week(day.getDay_of_week());

            return scheduleDaysRepository.save(existingDay);
        }

        return null;
    }

    // Delete Schedule Day
    public String deleteScheduleDay(Long id) {

        scheduleDaysRepository.deleteById(id);

        return "Schedule Day Deleted Successfully";
    }
}
