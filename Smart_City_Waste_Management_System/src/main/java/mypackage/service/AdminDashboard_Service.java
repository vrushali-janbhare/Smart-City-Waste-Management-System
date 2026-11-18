
package mypackage.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.enums.ComplaintStatus;
import mypackage.model.Complaint;
import mypackage.repository.User_Repository;
import mypackage.repository.Worker_Repository;
import mypackage.repository.Complaint_Repository;
import mypackage.repository.BulkPickupRequest_Repository;

@Service
public class AdminDashboard_Service {

    @Autowired
    private User_Repository userRepository;

    @Autowired
    private Worker_Repository workerRepository;

    @Autowired
    private Complaint_Repository complaintRepository;

    @Autowired
    private BulkPickupRequest_Repository bulkPickupRepository;

    // 1. Six dashboard statistics
    @Transactional(readOnly = true)
    public Map<String, Long> getDashboardStatistics() {

        Map<String, Long> stats = new LinkedHashMap<>();

        stats.put("totalCitizens",
                userRepository.countByRole_RoleName("USER"));

        stats.put("totalWorkers",
                workerRepository.count());

        stats.put("totalComplaints",
                complaintRepository.count());

        stats.put("pendingComplaints",
                complaintRepository.countByStatus(
                        ComplaintStatus.PENDING));

        stats.put("resolvedComplaints",
                complaintRepository.countByStatus(
                        ComplaintStatus.RESOLVED));

        stats.put("totalBulkPickups",
                bulkPickupRepository.count());

        return stats;
    }

    // 2. Complaint chart statistics
    @Transactional(readOnly = true)
    public Map<String, Long> getComplaintChartData() {

        Map<String, Long> chartData = new LinkedHashMap<>();

        for (ComplaintStatus status : ComplaintStatus.values()) {

            chartData.put(
                    status.name(),
                    complaintRepository.countByStatus(status)
            );
        }

        return chartData;
    }

    // 3. Five most recent complaints
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRecentComplaints() {

        List<Complaint> complaints =
                complaintRepository
                    .findTop5ByOrderByCreatedAtDescComplaintIdDesc();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Complaint complaint : complaints) {

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("complaintId", complaint.getComplaintId());

            item.put("citizenName",
                    complaint.getUser() != null
                        ? complaint.getUser().getFullName()
                        : "Unknown");

            item.put("categoryName",
                    complaint.getCategory() != null
                        ? complaint.getCategory().getCategoryName()
                        : "Not specified");

            item.put("status",
                    complaint.getStatus() != null
                        ? complaint.getStatus().name()
                        : "Unknown");

            item.put("createdAt", complaint.getCreatedAt());

            result.add(item);
        }

        return result;
    }
}
