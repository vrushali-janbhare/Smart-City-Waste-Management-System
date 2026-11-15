
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_address", nullable = false, length = 255)
    private String locationAddress;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

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

    // One complaint can have many notifications
    @OneToMany(mappedBy = "complaint")
    @JsonIgnore
    private Set<Notification> notifications;

    // One complaint can have one feedback
    @OneToOne(mappedBy = "complaint")
    @JsonIgnore
    private Feedback feedback;

    // One complaint can have many fines
    @OneToMany(mappedBy = "complaint")
    @JsonIgnore
    private Set<Fine> fines;

    // One complaint can have many AI classifications
    @OneToMany(mappedBy = "complaint")
    @JsonIgnore
    private Set<AiClassification> aiClassifications;


    // Automatically set creation date and time before inserting
    @PrePersist
    public void setCreatedAtAutomatically() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Set<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Set<Fine> getFines() {
        return fines;
    }

    public void setFines(Set<Fine> fines) {
        this.fines = fines;
    }

    public Set<AiClassification> getAiClassifications() {
        return aiClassifications;
    }

    public void setAiClassifications(Set<AiClassification> aiClassifications) {
        this.aiClassifications = aiClassifications;
    }
}
