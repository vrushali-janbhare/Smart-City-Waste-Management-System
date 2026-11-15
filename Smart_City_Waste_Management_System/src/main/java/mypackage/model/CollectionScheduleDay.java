
package mypackage.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "collection_schedule_days")
public class CollectionScheduleDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_day_id")
    private Long scheduleDayId;

    // Many schedule days belong to one collection schedule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CollectionSchedule schedule;
   

    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek;


    // Getters and Setters

    public Long getScheduleDayId() {
        return scheduleDayId;
    }

    public void setScheduleDayId(Long scheduleDayId) {
        this.scheduleDayId = scheduleDayId;
    }

    public CollectionSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(CollectionSchedule schedule) {
        this.schedule = schedule;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
