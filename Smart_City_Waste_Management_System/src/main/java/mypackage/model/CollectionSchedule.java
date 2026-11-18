
package mypackage.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "collection_schedules")
public class CollectionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    // Name of the collection schedule
    @Column(name = "schedule_name", nullable = false, length = 100)
    private String scheduleName;

    // Many schedules can belong to one area
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Area area;

    // Many schedules can use one vehicle
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Vehicle vehicle;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "schedule_status", nullable = false, length = 30)
    private String scheduleStatus;

    // One schedule has multiple collection days
    @OneToMany(mappedBy = "schedule")
    @JsonIgnore
    private Set<CollectionScheduleDay> scheduleDays;

    // One schedule has multiple crew members
    @OneToMany(mappedBy = "schedule")
    @JsonIgnore
    private Set<CollectionCrewMember> crewMembers;

    // One schedule has multiple collection records
    @OneToMany(mappedBy = "schedule")
    @JsonIgnore
    private Set<CollectionRecord> collectionRecords;

    // Getters and Setters

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Set<CollectionScheduleDay> getScheduleDays() {
        return scheduleDays;
    }

    public void setScheduleDays(Set<CollectionScheduleDay> scheduleDays) {
        this.scheduleDays = scheduleDays;
    }

    public Set<CollectionCrewMember> getCrewMembers() {
        return crewMembers;
    }

    public void setCrewMembers(Set<CollectionCrewMember> crewMembers) {
        this.crewMembers = crewMembers;
    }

    public Set<CollectionRecord> getCollectionRecords() {
        return collectionRecords;
    }

    public void setCollectionRecords(Set<CollectionRecord> collectionRecords) {
        this.collectionRecords = collectionRecords;
    }
}
