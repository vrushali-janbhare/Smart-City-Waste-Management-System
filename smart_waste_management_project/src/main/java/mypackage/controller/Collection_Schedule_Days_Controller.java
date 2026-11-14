
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Collection_Schedule_Days;
import mypackage.service.Collection_Schedule_Days_Service;

@RestController
@RequestMapping("/api/collection-schedule-days")
public class Collection_Schedule_Days_Controller {

    @Autowired
    private Collection_Schedule_Days_Service scheduleDaysService;

    // Add Schedule Day
    @PostMapping
    public Collection_Schedule_Days addScheduleDay(
            @RequestBody Collection_Schedule_Days day) {

        return scheduleDaysService.addScheduleDay(day);
    }

    // Get Schedule Day By ID
    @GetMapping("/{id}")
    public Collection_Schedule_Days getScheduleDayById(
            @PathVariable Long id) {

        return scheduleDaysService.getScheduleDayById(id);
    }

    // Get All Schedule Days
    @GetMapping
    public List<Collection_Schedule_Days> getAllScheduleDays() {

        return scheduleDaysService.getAllScheduleDays();
    }

    // Update Schedule Day
    @PutMapping("/{id}")
    public Collection_Schedule_Days updateScheduleDay(
            @PathVariable Long id,
            @RequestBody Collection_Schedule_Days day) {

        return scheduleDaysService.updateScheduleDay(id, day);
    }

    // Delete Schedule Day
    @DeleteMapping("/{id}")
    public String deleteScheduleDay(@PathVariable Long id) {

        return scheduleDaysService.deleteScheduleDay(id);
    }
}
