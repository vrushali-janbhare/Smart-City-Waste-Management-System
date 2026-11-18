
package mypackage.controller;

import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mypackage.service.AdminCitizen_Service;

@RestController
@RequestMapping("/api/admin/citizens")
public class AdminCitizen_Controller {

    @Autowired
    private AdminCitizen_Service adminCitizenService;

    // Get all citizens (ADMIN only)
    @GetMapping
    public ResponseEntity<?> getAllCitizens(HttpSession session) {

        Object userId =
                session.getAttribute("loggedInUserId");

        String role =
                (String) session.getAttribute("loggedInUserRole");

        if (userId == null) {

            return ResponseEntity.status(401).body(
                    Collections.singletonMap(
                            "message", "Please login first"
                    )
            );
        }

        if (!"ADMIN".equals(role)) {

            return ResponseEntity.status(403).body(
                    Collections.singletonMap(
                            "message", "Administrator access required"
                    )
            );
        }

        return ResponseEntity.ok(
                adminCitizenService.getAllCitizens()
        );
    }
}
