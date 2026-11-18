
package mypackage.model;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "worker_id")
    private Long workerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @Column(
        name = "employee_code",
        nullable = false,
        unique = true,
        length = 30
    )
    private String employeeCode;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @OneToMany(mappedBy = "worker")
    @JsonIgnore
    private Set<CollectionCrewMember> crewMembers;

    @OneToMany(mappedBy = "worker")
    @JsonIgnore
    private Set<CollectionRecord> collectionRecords;

    // Automatically set joining date
    @PrePersist
    public void setJoiningDateAutomatically() {
        if (this.joiningDate == null) {
            this.joiningDate = LocalDate.now();
        }
    }

    // Getters and Setters

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Set<CollectionCrewMember> getCrewMembers() {
        return crewMembers;
    }

    public void setCrewMembers(
            Set<CollectionCrewMember> crewMembers) {
        this.crewMembers = crewMembers;
    }

    public Set<CollectionRecord> getCollectionRecords() {
        return collectionRecords;
    }

    public void setCollectionRecords(
            Set<CollectionRecord> collectionRecords) {
        this.collectionRecords = collectionRecords;
    }
}
