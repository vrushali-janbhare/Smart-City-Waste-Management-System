
package mypackage.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Role role;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "phone_number", length = 15, unique = true)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;


    // 1. User - Worker
    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Worker worker;


    // 2. User - Complaint
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Complaint> complaints;


    // 3. User - BulkPickupRequest
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<BulkPickupRequest> bulkPickupRequests;


    // 4. User - ComplaintStatusHistory
    @OneToMany(mappedBy = "changedBy")
    @JsonIgnore
    private Set<ComplaintStatusHistory> statusHistories;


    public User() {
    }


    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Set<Complaint> getComplaints() {
        return complaints;
    }

    public void setComplaints(Set<Complaint> complaints) {
        this.complaints = complaints;
    }

    public Set<BulkPickupRequest> getBulkPickupRequests() {
        return bulkPickupRequests;
    }

    public void setBulkPickupRequests(Set<BulkPickupRequest> bulkPickupRequests) {
        this.bulkPickupRequests = bulkPickupRequests;
    }

    public Set<ComplaintStatusHistory> getStatusHistories() {
        return statusHistories;
    }

    public void setStatusHistories(Set<ComplaintStatusHistory> statusHistories) {
        this.statusHistories = statusHistories;
    }
}
