
package mypackage.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CollectionScheduleRequestDTO {

    private String scheduleName;

    private Long areaId;

    private Long vehicleId;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String scheduleStatus;

    private List<String> collectionDays;

    private List<Long> workerIds;

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
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

    public List<String> getCollectionDays() {
        return collectionDays;
    }

    public void setCollectionDays(List<String> collectionDays) {
        this.collectionDays = collectionDays;
    }

    public List<Long> getWorkerIds() {
        return workerIds;
    }

    public void setWorkerIds(List<Long> workerIds) {
        this.workerIds = workerIds;
    }
}
