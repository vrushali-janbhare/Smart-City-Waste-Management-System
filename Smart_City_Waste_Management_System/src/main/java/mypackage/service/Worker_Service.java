
package mypackage.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import mypackage.dto.WorkerProfileUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.dto.WorkerRegistrationDTO;
import mypackage.dto.WorkerResponseDTO;
import mypackage.dto.WorkerUpdateDTO;

import mypackage.enums.ComplaintStatus;

import mypackage.model.CollectionCrewMember;
import mypackage.model.CollectionSchedule;
import mypackage.model.Complaint;
import mypackage.model.ComplaintStatusHistory;
import mypackage.model.Role;
import mypackage.model.User;
import mypackage.model.Worker;

import mypackage.repository.CollectionCrewMember_Repository;
import mypackage.repository.ComplaintStatusHistory_Repository;
import mypackage.repository.Complaint_Repository;
import mypackage.repository.Role_Repository;
import mypackage.repository.User_Repository;
import mypackage.repository.Worker_Repository;

@Service
public class Worker_Service {

    @Autowired
    private Worker_Repository workerRepository;

    @Autowired
    private User_Repository userRepository;

    @Autowired
    private Role_Repository roleRepository;

    @Autowired
    private Complaint_Repository complaintRepository;

    @Autowired
    private CollectionCrewMember_Repository crewMemberRepository;

    @Autowired
    private ComplaintStatusHistory_Repository statusHistoryRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // ==========================================
    // 1. REGISTER NEW WORKER
    // ==========================================

    @Transactional
    public Worker registerWorker(WorkerRegistrationDTO request) {

        if (request == null ||
                request.getFullName() == null ||
                request.getEmail() == null ||
                request.getPhoneNumber() == null ||
                request.getAddress() == null ||
                request.getPassword() == null ||
                request.getEmployeeCode() == null) {

            throw new IllegalArgumentException(
                    "All worker fields are required");
        }

        String fullName = request.getFullName().trim();

        String email = request.getEmail()
                .trim().toLowerCase(Locale.ROOT);

        String phone = request.getPhoneNumber().trim();

        String address = request.getAddress().trim();

        String password = request.getPassword();

        String employeeCode = request.getEmployeeCode()
                .trim().toUpperCase(Locale.ROOT);

        if (fullName.isEmpty() ||
                email.isEmpty() ||
                phone.isEmpty() ||
                address.isEmpty() ||
                password.trim().isEmpty() ||
                employeeCode.isEmpty()) {

            throw new IllegalArgumentException(
                    "All worker fields are required");
        }

        validateWorkerFields(
                fullName, email, phone, address, employeeCode);

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        if (userRepository.findByPhoneNumber(phone).isPresent()) {
            throw new IllegalArgumentException(
                    "Phone number is already registered");
        }

        if (workerRepository.existsByEmployeeCode(employeeCode)) {
            throw new IllegalArgumentException(
                    "Employee code already exists");
        }

        Role workerRole = roleRepository.findByRoleName("WORKER")
                .orElseThrow(() -> new IllegalStateException(
                        "WORKER role not found in database"));

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setAddress(address);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(workerRole);

        User savedUser = userRepository.save(user);

        Worker worker = new Worker();

        worker.setUser(savedUser);
        worker.setEmployeeCode(employeeCode);

        return workerRepository.save(worker);
    }


    // ==========================================
    // 2. VALIDATE WORKER FIELDS
    // ==========================================

    private void validateWorkerFields(
            String fullName,
            String email,
            String phone,
            String address,
            String employeeCode) {

        if (fullName.length() > 100 ||
                email.length() > 150 ||
                phone.length() > 15 ||
                address.length() > 255 ||
                employeeCode.length() > 30) {

            throw new IllegalArgumentException(
                    "One or more worker fields are too long");
        }

        if (!email.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "Enter a valid email address");
        }

        if (!phone.matches("^[0-9]{10}$")) {

            throw new IllegalArgumentException(
                    "Enter a valid 10-digit phone number");
        }
    }


    // ==========================================
    // 3. CONVERT WORKER TO RESPONSE DTO
    // ==========================================

    private WorkerResponseDTO convertToDTO(Worker worker) {

        User user = worker.getUser();

        long assignedComplaintCount =
                complaintRepository.countByWorker_WorkerId(
                        worker.getWorkerId());

        return new WorkerResponseDTO(
                worker.getWorkerId(),
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                worker.getEmployeeCode(),
                worker.getJoiningDate(),
                assignedComplaintCount
        );
    }


    // ==========================================
    // 4. GET ALL WORKER DETAILS
    // ==========================================

    @Transactional(readOnly = true)
    public List<WorkerResponseDTO> getAllWorkerDetails() {

        return workerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // ==========================================
    // 5. GET WORKER DETAILS BY ID
    // ==========================================

    @Transactional(readOnly = true)
    public Optional<WorkerResponseDTO> getWorkerDetailsById(
            Long workerId) {

        return workerRepository.findById(workerId)
                .map(this::convertToDTO);
    }


    // ==========================================
    // 6. REGISTER WORKER AND RETURN DTO
    // ==========================================

    @Transactional
    public WorkerResponseDTO registerWorkerWithDetails(
            WorkerRegistrationDTO request) {

        Worker savedWorker = registerWorker(request);

        return convertToDTO(savedWorker);
    }


    // ==========================================
    // 7. UPDATE WORKER DETAILS
    // ==========================================

    @Transactional
    public WorkerResponseDTO updateWorker(
            Long workerId,
            WorkerUpdateDTO request) {

        if (request == null ||
                request.getFullName() == null ||
                request.getEmail() == null ||
                request.getPhoneNumber() == null ||
                request.getAddress() == null ||
                request.getEmployeeCode() == null) {

            throw new IllegalArgumentException(
                    "All worker fields are required");
        }

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Worker not found")
                );

        User user = worker.getUser();

        String fullName = request.getFullName().trim();

        String email = request.getEmail()
                .trim().toLowerCase(Locale.ROOT);

        String phone = request.getPhoneNumber().trim();

        String address = request.getAddress().trim();

        String employeeCode = request.getEmployeeCode()
                .trim().toUpperCase(Locale.ROOT);

        if (fullName.isEmpty() ||
                email.isEmpty() ||
                phone.isEmpty() ||
                address.isEmpty() ||
                employeeCode.isEmpty()) {

            throw new IllegalArgumentException(
                    "All worker fields are required");
        }

        validateWorkerFields(
                fullName, email, phone, address, employeeCode);

        Optional<User> existingEmail =
                userRepository.findByEmail(email);

        if (existingEmail.isPresent() &&
                !existingEmail.get().getUserId()
                        .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        Optional<User> existingPhone =
                userRepository.findByPhoneNumber(phone);

        if (existingPhone.isPresent() &&
                !existingPhone.get().getUserId()
                        .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "Phone number is already registered");
        }

        if (!employeeCode.equals(worker.getEmployeeCode())
                && workerRepository.existsByEmployeeCode(
                        employeeCode)) {

            throw new IllegalArgumentException(
                    "Employee code already exists");
        }

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setAddress(address);

        userRepository.save(user);

        worker.setEmployeeCode(employeeCode);

        Worker updatedWorker =
                workerRepository.save(worker);

        return convertToDTO(updatedWorker);
    }


    // ==========================================
    // 8. EXISTING METHODS
    // ==========================================

    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public Optional<Worker> getWorkerById(Long workerId) {
        return workerRepository.findById(workerId);
    }

    public void deleteWorker(Long workerId) {
        workerRepository.deleteById(workerId);
    }


    // ==========================================
    // 9. FIND CURRENT LOGGED-IN WORKER
    // ==========================================

    private Worker getWorkerByUserId(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Invalid user session");
        }

        return workerRepository.findByUser_UserId(userId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Worker account not found"));
    }


    // ==========================================
    // 10. WORKER PROFILE
    // ==========================================

    @Transactional(readOnly = true)
    public WorkerResponseDTO getMyWorkerProfile(Long userId) {

        Worker worker = getWorkerByUserId(userId);

        return convertToDTO(worker);
    }


    // ==========================================
    // 11. WORKER DASHBOARD STATISTICS
    // ==========================================

    @Transactional(readOnly = true)
    public Map<String, Long> getMyDashboardStats(
            Long userId) {

        Worker worker = getWorkerByUserId(userId);

        Long workerId = worker.getWorkerId();

        Map<String, Long> stats = new LinkedHashMap<>();

        stats.put("totalComplaints",
                complaintRepository.countByWorker_WorkerId(
                        workerId));

        stats.put("assignedComplaints",
                complaintRepository.countByWorker_WorkerIdAndStatus(
                        workerId,
                        ComplaintStatus.ASSIGNED));

        stats.put("inProgressComplaints",
                complaintRepository.countByWorker_WorkerIdAndStatus(
                        workerId,
                        ComplaintStatus.IN_PROGRESS));

        stats.put("resolvedComplaints",
                complaintRepository.countByWorker_WorkerIdAndStatus(
                        workerId,
                        ComplaintStatus.RESOLVED));

        return stats;
    }


    // ==========================================
    // 12. CONVERT COMPLAINT TO DASHBOARD DATA
    // ==========================================

    private Map<String, Object> complaintToMap(
            Complaint complaint) {

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("complaintId", complaint.getComplaintId());
        data.put("description", complaint.getDescription());
        data.put("locationAddress",
                complaint.getLocationAddress());
        data.put("categoryName",
                complaint.getCategoryName());
        data.put("areaId", complaint.getAreaId());
        data.put("areaName", complaint.getAreaName());
        data.put("status", complaint.getStatus());
        data.put("createdAt", complaint.getCreatedAt());
        data.put("resolvedAt", complaint.getResolvedAt());
        data.put("latitude", complaint.getLatitude());
        data.put("longitude", complaint.getLongitude());

        return data;
    }


    // ==========================================
    // 13. WORKER'S ASSIGNED COMPLAINTS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMyComplaints(
            Long userId) {

        Worker worker = getWorkerByUserId(userId);

        List<Complaint> complaints =
                complaintRepository
                    .findByWorker_WorkerIdOrderByCreatedAtDesc(
                            worker.getWorkerId());

        return complaints.stream()
                .map(this::complaintToMap)
                .collect(Collectors.toList());
    }


    // ==========================================
    // 14. UPDATE ASSIGNED COMPLAINT STATUS
    // ==========================================

    @Transactional
    public Map<String, Object> updateMyComplaintStatus(
            Long userId,
            Long complaintId,
            ComplaintStatus newStatus) {

        Worker worker = getWorkerByUserId(userId);

        if (complaintId == null || newStatus == null) {
            throw new IllegalArgumentException(
                    "Complaint ID and status are required");
        }

        Complaint complaint = complaintRepository
                .findByComplaintIdAndWorker_WorkerId(
                        complaintId,
                        worker.getWorkerId())
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Complaint not found or not assigned to you"));

        ComplaintStatus oldStatus = complaint.getStatus();

        boolean validTransition =
                (oldStatus == ComplaintStatus.ASSIGNED
                        && newStatus == ComplaintStatus.IN_PROGRESS)
                ||
                (oldStatus == ComplaintStatus.IN_PROGRESS
                        && newStatus == ComplaintStatus.RESOLVED);

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid status change. Workers can only "
                    + "change ASSIGNED to IN_PROGRESS or "
                    + "IN_PROGRESS to RESOLVED");
        }

        complaint.setStatus(newStatus);

        if (newStatus == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint savedComplaint =
                complaintRepository.save(complaint);

        ComplaintStatusHistory history =
                new ComplaintStatusHistory();

        history.setComplaint(savedComplaint);
        history.setChangedBy(worker.getUser());
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setRemarks("Updated by assigned worker");

        statusHistoryRepository.save(history);

        return complaintToMap(savedComplaint);
    }


    // ==========================================
    // 15. WORKER'S COLLECTION SCHEDULES
    // ==========================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMySchedules(
            Long userId) {

        Worker worker = getWorkerByUserId(userId);

        List<CollectionCrewMember> crewMembers =
                crewMemberRepository.findByWorker_WorkerId(
                        worker.getWorkerId());

        List<Map<String, Object>> schedules =
                new ArrayList<>();

        for (CollectionCrewMember member : crewMembers) {

            CollectionSchedule schedule =
                    member.getSchedule();

            Map<String, Object> data =
                    new LinkedHashMap<>();

            data.put("scheduleId",
                    schedule.getScheduleId());

            data.put("scheduleName",
                    schedule.getScheduleName());

            data.put("startDate",
                    schedule.getStartDate());

            data.put("endDate",
                    schedule.getEndDate());

            data.put("startTime",
                    schedule.getStartTime());

            data.put("endTime",
                    schedule.getEndTime());

            data.put("scheduleStatus",
                    schedule.getScheduleStatus());

            if (schedule.getArea() != null) {
                data.put("areaName",
                        schedule.getArea().getAreaName());
            } else {
                data.put("areaName", null);
            }

            if (schedule.getVehicle() != null) {
                data.put("vehicleNumber",
                        schedule.getVehicle().getVehicleNumber());
            } else {
                data.put("vehicleNumber", null);
            }

            schedules.add(data);
        }

        return schedules;
    }

    // ==========================================
    // 16. UPDATE LOGGED-IN WORKER PROFILE
    // ==========================================

    @Transactional
    public WorkerResponseDTO updateMyWorkerProfile(
            Long userId,
            WorkerProfileUpdateDTO request) {

        // Check request
        if (request == null ||
                request.getFullName() == null ||
                request.getPhoneNumber() == null ||
                request.getAddress() == null) {

            throw new IllegalArgumentException(
                    "Full name, phone number and address are required");
        }

        // Find worker using logged-in user ID
        Worker worker = getWorkerByUserId(userId);

        User user = worker.getUser();

        // Get updated values
        String fullName = request.getFullName().trim();
        String phone = request.getPhoneNumber().trim();
        String address = request.getAddress().trim();

        // Check empty fields
        if (fullName.isEmpty() ||
                phone.isEmpty() ||
                address.isEmpty()) {

            throw new IllegalArgumentException(
                    "All profile fields are required");
        }

        // Validate full name
        if (fullName.length() > 100) {

            throw new IllegalArgumentException(
                    "Full name cannot exceed 100 characters");
        }

        // Validate phone number
        if (!phone.matches("^[0-9]{10}$")) {

            throw new IllegalArgumentException(
                    "Enter a valid 10-digit phone number");
        }

        // Validate address
        if (address.length() > 255) {

            throw new IllegalArgumentException(
                    "Address cannot exceed 255 characters");
        }

        // Check whether phone belongs to another user
        Optional<User> existingPhone =
                userRepository.findByPhoneNumber(phone);

        if (existingPhone.isPresent() &&
                !existingPhone.get().getUserId()
                        .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "Phone number is already registered");
        }

        // Update only allowed fields
        user.setFullName(fullName);
        user.setPhoneNumber(phone);
        user.setAddress(address);

        // Save changes in users table
        userRepository.save(user);

        // Return updated worker details
        return convertToDTO(worker);
    }

}
