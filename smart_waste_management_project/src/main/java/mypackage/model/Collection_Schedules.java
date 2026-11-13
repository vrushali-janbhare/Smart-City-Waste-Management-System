package mypackage.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="collection_schedules")
public class Collection_Schedules {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int schedule_id;
	

	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="vehicle_id",nullable=false)
	private Vehicles vehicle;

	
	@Column(name="schedule_name",length=100,nullable=false)
	private String schedule_name;

	
	@Column(name="collection_date",nullable=false)
	private LocalDate collection_date;

	
	@Column(name="start_time",nullable=false)
	private LocalTime start_time;

	
	@Column(name="end_time",nullable=false)
	private LocalTime end_time;

	
	@Column(name="status",length=20,nullable=false)
	@Enumerated(EnumType.STRING)
	private ScheduleStatus  status;

	@OneToMany(mappedBy = "collection_schedule")
	@JsonIgnoreProperties("collection_schedule")
	private Set<Collection_Schedule_Days> days;
	
	@OneToMany(mappedBy = "collection_schedule")
	@JsonIgnoreProperties("collection_schedule")
	private Set<Collection_Crew_Members>Collection_Crew_Member;
	
	@OneToMany(mappedBy = "collection_schedule")
	@JsonIgnoreProperties("collection_schedule")
	private Set<Collection_Records>Collection_Record;
	@Column(name="created_at",nullable=false)
	private LocalDateTime created_at;

}
