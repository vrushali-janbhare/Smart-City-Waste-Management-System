
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Collection_Schedules;
import mypackage.service.Collection_Schedules_Service;

@RestController
@RequestMapping("/api/collection-schedules")
public class Collection_Schedules_Controller {

    @Autowired
    private Collection_Schedules_Service scheduleService;

    // Add Schedule
    @PostMapping
    public Collection_Schedules addSchedule(
            @RequestBody Collection_Schedules schedule) {

        return scheduleService.addSchedule(schedule);
    }

    // Get Schedule By ID
    @GetMapping("/{id}")
    public Collection_Schedules getScheduleById(
            @PathVariable Long id) {

        return scheduleService.getScheduleById(id);
    }

    // Get All Schedules
    @GetMapping
    public List<Collection_Schedules> getAllSchedules() {

        return scheduleService.getAllSchedules();
    }

    // Update Schedule
    @PutMapping("/{id}")
    public Collection_Schedules updateSchedule(
            @PathVariable Long id,
            @RequestBody Collection_Schedules schedule) {

        return scheduleService.updateSchedule(id, schedule);
    }

    // Delete Schedule
    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id) {

        return scheduleService.deleteSchedule(id);
    }
}
