
package mypackage.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "waste_categories")
public class WasteCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", length = 100, nullable = false, unique = true)
    private String categoryName;

    @Column(name = "is_recyclable", nullable = false)
    private boolean isRecyclable;

    @Column(name = "is_hazardous", nullable = false)
    private boolean isHazardous;

    // One waste category can have many complaints
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Complaint> complaints;

    // One waste category can have many collection records
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<CollectionRecord> collectionRecords;

    // One waste category can have many bulk pickup requests
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<BulkPickupRequest> bulkPickupRequests;

    public WasteCategory() {
    }

    // Getters and Setters

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isRecyclable() {
        return isRecyclable;
    }

    public void setRecyclable(boolean recyclable) {
        isRecyclable = recyclable;
    }

    public boolean isHazardous() {
        return isHazardous;
    }

    public void setHazardous(boolean hazardous) {
        this.isHazardous = hazardous;
    }

    public Set<Complaint> getComplaints() {
        return complaints;
    }

    public void setComplaints(Set<Complaint> complaints) {
        this.complaints = complaints;
    }

    public Set<CollectionRecord> getCollectionRecords() {
        return collectionRecords;
    }

    public void setCollectionRecords(Set<CollectionRecord> collectionRecords) {
        this.collectionRecords = collectionRecords;
    }

    public Set<BulkPickupRequest> getBulkPickupRequests() {
        return bulkPickupRequests;
    }

    public void setBulkPickupRequests(Set<BulkPickupRequest> bulkPickupRequests) {
        this.bulkPickupRequests = bulkPickupRequests;
    }
}
