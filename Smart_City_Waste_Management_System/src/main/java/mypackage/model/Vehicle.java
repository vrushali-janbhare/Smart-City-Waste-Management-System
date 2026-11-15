
package mypackage.model;

import java.math.BigDecimal;
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
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vehicle_number", length = 30, nullable = false, unique = true)
    private String vehicleNumber;

    @Column(name = "vehicle_type", length = 50, nullable = false)
    private String vehicleType;

    @Column(name = "capacity_kg", precision = 10, scale = 2, nullable = false)
    private BigDecimal capacityKg;

    @Column(name = "vehicle_status", length = 30, nullable = false)
    private String vehicleStatus;


    // 1. Vehicle - CollectionSchedule
    @OneToMany(mappedBy = "vehicle")
    @JsonIgnore
    private Set<CollectionSchedule> collectionSchedules;


    public Vehicle() {
    }


    // Getters and Setters

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getCapacityKg() {
        return capacityKg;
    }

    public void setCapacityKg(BigDecimal capacityKg) {
        this.capacityKg = capacityKg;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Set<CollectionSchedule> getCollectionSchedules() {
        return collectionSchedules;
    }

    public void setCollectionSchedules(Set<CollectionSchedule> collectionSchedules) {
        this.collectionSchedules = collectionSchedules;
    }
}
