package mypackage.model;



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
    private int schedule_day_id;
        
    @ManyToOne(fetch  = FetchType.LAZY, optional = false)
    @JoinColumn(name="schedule_id",nullable=false)
    private Collection_Schedules  collection_schedule;
    
    @Enumerated(EnumType.STRING)
    private  DayOfWeek  day_of_week;
	

}
