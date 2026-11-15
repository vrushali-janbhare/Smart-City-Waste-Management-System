
package mypackage.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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


    // 1. WasteCategory - Complaint
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Complaint> complaints;


    // 2. WasteCategory - CollectionRecord
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<CollectionRecord> collectionRecords;


    // 3. WasteCategory - BulkPickupRequest
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<BulkPickupRequest> bulkPickupRequests;


    // 4. WasteCategory - AiClassification
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<AiClassification> aiClassifications;


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
        isHazardous = hazardous;
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

    public Set<AiClassification> getAiClassifications() {
        return aiClassifications;
    }

    public void setAiClassifications(Set<AiClassification> aiClassifications) {
        this.aiClassifications = aiClassifications;
    }
}
