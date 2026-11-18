
package mypackage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.PrePersist;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import mypackage.enums.ComplaintStatus;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long complaintId;

    // Many complaints can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    // Many complaints can belong to one area
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Area area;

    // Many complaints can belong to one waste category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private WasteCategory category;

    // NEW: Many complaints can be assigned to one worker
    // worker_id is nullable until ADMIN assigns a worker
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = true)
    @JsonIgnore
    private Worker worker;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_address", nullable = false, length = 255)
    private String locationAddress;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    // Complaint current status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ComplaintStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // One complaint can have many images
    @OneToMany(mappedBy = "complaint")
    @JsonIgnore
    private Set<ComplaintImage> complaintImages;

    // One complaint can have many status history records
    @OneToMany(mappedBy = "complaint")
    @JsonIgnore
    private Set<ComplaintStatusHistory> statusHistories;

    // One complaint can have one feedback
    @OneToOne(mappedBy = "complaint")
    @JsonIgnore
    private Feedback feedback;

    // Automatically set values before inserting a new complaint
    @PrePersist
    public void setDefaultsBeforeInsert() {

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = ComplaintStatus.PENDING;
        }
    }

    // Getters and Setters

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public WasteCategory getCategory() {
        return category;
    }

    public void setCategory(WasteCategory category) {
        this.category = category;
    }

    // NEW: Worker getters and setters
    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    // Expose only the waste category name in JSON
    @JsonProperty("categoryName")
    public String getCategoryName() {
        return category != null
                ? category.getCategoryName()
                : null;
    }

    // Expose area ID in JSON
    @JsonProperty("areaId")
    public Long getAreaId() {
        return area != null
                ? area.getAreaId()
                : null;
    }

    // Expose area name in JSON
    @JsonProperty("areaName")
    public String getAreaName() {
        return area != null
                ? area.getAreaName()
                : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Set<ComplaintImage> getComplaintImages() {
        return complaintImages;
    }

    public void setComplaintImages(Set<ComplaintImage> complaintImages) {
        this.complaintImages = complaintImages;
    }

    public Set<ComplaintStatusHistory> getStatusHistories() {
        return statusHistories;
    }

    public void setStatusHistories(Set<ComplaintStatusHistory> statusHistories) {
        this.statusHistories = statusHistories;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}
