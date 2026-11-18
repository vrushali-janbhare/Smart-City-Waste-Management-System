
package mypackage.dto;

import java.time.LocalDate;

public class WorkerResponseDTO {

    private Long workerId;
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String employeeCode;
    private LocalDate joiningDate;

    // New field
    private long assignedComplaintCount;

    // Default constructor
    public WorkerResponseDTO() {
    }

    // Existing constructor
    public WorkerResponseDTO(
            Long workerId,
            Long userId,
            String fullName,
            String email,
            String phoneNumber,
            String address,
            String employeeCode,
            LocalDate joiningDate) {

        this.workerId = workerId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.employeeCode = employeeCode;
        this.joiningDate = joiningDate;
    }

    // New constructor with assigned complaint count
    public WorkerResponseDTO(
            Long workerId,
            Long userId,
            String fullName,
            String email,
            String phoneNumber,
            String address,
            String employeeCode,
            LocalDate joiningDate,
            long assignedComplaintCount) {

        this(workerId, userId, fullName, email,
                phoneNumber, address, employeeCode, joiningDate);

        this.assignedComplaintCount = assignedComplaintCount;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    // New getter
    public long getAssignedComplaintCount() {
        return assignedComplaintCount;
    }

    // New setter
    public void setAssignedComplaintCount(long assignedComplaintCount) {
        this.assignedComplaintCount = assignedComplaintCount;
    }
}
