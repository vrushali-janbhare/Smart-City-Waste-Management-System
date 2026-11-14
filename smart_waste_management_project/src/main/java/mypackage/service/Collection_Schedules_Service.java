
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Collection_Schedules;
import mypackage.repository.Collection_Schedules_Repository;

@Service
public class Collection_Schedules_Service {

    @Autowired
    private Collection_Schedules_Repository scheduleRepository;

    // Add Collection Schedule
    public Collection_Schedules addSchedule(Collection_Schedules schedule) {
        return scheduleRepository.save(schedule);
    }

    // Get Schedule By ID
    public Collection_Schedules getScheduleById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    // Get All Schedules
    public List<Collection_Schedules> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Update Schedule
    public Collection_Schedules updateSchedule(Long id, Collection_Schedules schedule) {

        Collection_Schedules existingSchedule =
                scheduleRepository.findById(id).orElse(null);

        if (existingSchedule != null) {

            existingSchedule.setVehicle(schedule.getVehicle());
            existingSchedule.setSchedule_name(schedule.getSchedule_name());
            existingSchedule.setCollection_date(schedule.getCollection_date());
            existingSchedule.setStart_time(schedule.getStart_time());
            existingSchedule.setEnd_time(schedule.getEnd_time());
            existingSchedule.setStatus(schedule.getStatus());
            existingSchedule.setAreas(schedule.getAreas());

            return scheduleRepository.save(existingSchedule);
        }

        return null;
    }

    // Delete Schedule
    public String deleteSchedule(Long id) {

        scheduleRepository.deleteById(id);

        return "Collection Schedule Deleted Successfully";
    }
}
