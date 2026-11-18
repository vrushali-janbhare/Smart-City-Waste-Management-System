
package mypackage.controller;

import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mypackage.service.AdminDashboard_Service;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboard_Controller {

    @Autowired
    private AdminDashboard_Service adminDashboardService;

    // Check whether the logged-in user is an administrator
    private ResponseEntity<?> checkAdmin(HttpSession session) {

        Object userId = session.getAttribute("loggedInUserId");
        String role = (String) session.getAttribute("loggedInUserRole");

        if (userId == null) {
            return ResponseEntity.status(401).body(
                    Collections.singletonMap(
                            "message", "Please login first"));
        }

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(
                    Collections.singletonMap(
                            "message", "Administrator access required"));
        }

        return null;
    }

    // 1. Dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics(HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        return ResponseEntity.ok(
                adminDashboardService.getDashboardStatistics());
    }

    // 2. Complaint chart
    @GetMapping("/complaint-chart")
    public ResponseEntity<?> getComplaintChart(HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        return ResponseEntity.ok(
                adminDashboardService.getComplaintChartData());
    }

    // 3. Recent complaints table
    @GetMapping("/recent-complaints")
    public ResponseEntity<?> getRecentComplaints(HttpSession session) {

        ResponseEntity<?> error = checkAdmin(session);

        if (error != null) {
            return error;
        }

        return ResponseEntity.ok(
                adminDashboardService.getRecentComplaints());
    }
}
