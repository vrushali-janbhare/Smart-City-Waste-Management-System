
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
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "area_name", length = 100, nullable = false)
    private String areaName;

    @Column(name = "ward_number", length = 20, unique = true)
    private String wardNumber;

    @Column(name = "city", length = 100, nullable = false)
    private String city;


    // 1. Area - Complaint
    @OneToMany(mappedBy = "area")
    @JsonIgnore
    private Set<Complaint> complaints;


    // 2. Area - CollectionSchedule
    @OneToMany(mappedBy = "area")
    @JsonIgnore
    private Set<CollectionSchedule> collectionSchedules;


    // 3. Area - BulkPickupRequest
    @OneToMany(mappedBy = "area")
    @JsonIgnore
    private Set<BulkPickupRequest> bulkPickupRequests;


    public Area() {
    }


    // Getters and Setters

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getWardNumber() {
        return wardNumber;
    }

    public void setWardNumber(String wardNumber) {
        this.wardNumber = wardNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<Complaint> getComplaints() {
        return complaints;
    }

    public void setComplaints(Set<Complaint> complaints) {
        this.complaints = complaints;
    }

    public Set<CollectionSchedule> getCollectionSchedules() {
        return collectionSchedules;
    }

    public void setCollectionSchedules(Set<CollectionSchedule> collectionSchedules) {
        this.collectionSchedules = collectionSchedules;
    }

    public Set<BulkPickupRequest> getBulkPickupRequests() {
        return bulkPickupRequests;
    }

    public void setBulkPickupRequests(Set<BulkPickupRequest> bulkPickupRequests) {
        this.bulkPickupRequests = bulkPickupRequests;
    }
}
