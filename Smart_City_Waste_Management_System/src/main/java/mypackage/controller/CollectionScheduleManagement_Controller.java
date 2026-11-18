
package mypackage.controller;

import java.util.List;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.dto.CollectionScheduleRequestDTO;
import mypackage.dto.CollectionScheduleResponseDTO;
import mypackage.service.CollectionScheduleManagement_Service;

@RestController
@RequestMapping("/api/admin/collection-schedules")
public class CollectionScheduleManagement_Controller {

    @Autowired
    private CollectionScheduleManagement_Service scheduleService;

    private boolean isAdmin(HttpSession session) {

        Object userId = session.getAttribute("loggedInUserId");
        Object role = session.getAttribute("loggedInUserRole");

        return userId != null
                && role != null
                && "ADMIN".equalsIgnoreCase(role.toString());
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(
            @RequestBody CollectionScheduleRequestDTO request,
            HttpSession session) {

        if (!isAdmin(session)) {
            return forbidden();
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(scheduleService.createSchedule(request));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSchedules(HttpSession session) {

        if (!isAdmin(session)) {
            return forbidden();
        }

        List<CollectionScheduleResponseDTO> schedules =
                scheduleService.getAllSchedules();

        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getScheduleById(
            @PathVariable Long scheduleId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return forbidden();
        }

        try {
            return ResponseEntity.ok(
                    scheduleService.getScheduleById(scheduleId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message(ex.getMessage()));
        }
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody CollectionScheduleRequestDTO request,
            HttpSession session) {

        if (!isAdmin(session)) {
            return forbidden();
        }

        try {
            return ResponseEntity.ok(
                    scheduleService.updateSchedule(scheduleId, request));
        } catch (IllegalArgumentException ex) {
            return scheduleError(ex);
        }
    }

    @PatchMapping("/{scheduleId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long scheduleId,
            @RequestBody Map<String, String> request,
            HttpSession session) {

        if (!isAdmin(session)) {
            return forbidden();
        }

        try {
            return ResponseEntity.ok(
                    scheduleService.updateScheduleStatus(
                            scheduleId, request.get("scheduleStatus")));
        } catch (IllegalArgumentException ex) {
            return scheduleError(ex);
        }
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long scheduleId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return forbidden();
        }

        try {
            scheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.noContent().build();

        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(message(ex.getMessage()));

        } catch (IllegalArgumentException ex) {
            return scheduleError(ex);
        }
    }

    private ResponseEntity<?> scheduleError(
            IllegalArgumentException ex) {

        if (ex.getMessage() != null
                && ex.getMessage().startsWith("Schedule not found")) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message(ex.getMessage()));
        }

        return badRequest(ex.getMessage());
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(message("Only ADMIN can manage collection schedules"));
    }

    private ResponseEntity<?> badRequest(String text) {
        return ResponseEntity.badRequest().body(message(text));
    }

    private Map<String, String> message(String text) {
        return Collections.singletonMap("message", text);
    }
}
