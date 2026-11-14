package mypackage.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="collection_schedule_days")
public class Collection_Schedule_Days {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long schedule_day_id;
        
    @ManyToOne(fetch  = FetchType.LAZY, optional = false)
    @JoinColumn(name="schedule_id",nullable=false)
    private Collection_Schedules  collection_schedule;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", length = 15, nullable = false)
    private  DayOfWeek  day_of_week;

	public long getSchedule_day_id() {
		return schedule_day_id;
	}

	public void setSchedule_day_id(long schedule_day_id) {
		this.schedule_day_id = schedule_day_id;
	}

	public Collection_Schedules getCollection_schedule() {
		return collection_schedule;
	}

	public void setCollection_schedule(Collection_Schedules collection_schedule) {
		this.collection_schedule = collection_schedule;
	}

	public DayOfWeek getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(DayOfWeek day_of_week) {
		this.day_of_week = day_of_week;
	}

	public Collection_Schedule_Days(long schedule_day_id, Collection_Schedules collection_schedule,
			DayOfWeek day_of_week) {
		super();
		this.schedule_day_id = schedule_day_id;
		this.collection_schedule = collection_schedule;
		this.day_of_week = day_of_week;
	}

	public Collection_Schedule_Days() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
