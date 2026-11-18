
package mypackage.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.dto.WorkerRegistrationDTO;
import mypackage.dto.WorkerResponseDTO;
import mypackage.dto.WorkerUpdateDTO;

import mypackage.model.Role;
import mypackage.model.User;
import mypackage.model.Worker;

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

        // Count complaints assigned to this worker
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

        // Update users table
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setAddress(address);

        userRepository.save(user);

        // Update workers table
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
}
