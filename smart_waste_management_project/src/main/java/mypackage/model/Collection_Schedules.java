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
	private long schedule_id;
	

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
   
	@Column(name="created_at",nullable=false)
	private LocalDateTime created_at;
	
	@OneToMany(mappedBy = "collection_schedule")
	@JsonIgnoreProperties("collection_schedule")
	private Set<Collection_Schedule_Days> days;
	
	@OneToMany(mappedBy = "collection_schedule")
	@JsonIgnoreProperties("collection_schedule")
	private Set<Collection_Crew_Members>Collection_Crew_Member;
	
	@OneToMany(mappedBy = "collection_schedule")
	@JsonIgnoreProperties("collection_schedule")
	private Set<Collection_Records>Collection_Record;
	

   
	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="area_id",nullable=false)
	private Area areas;



	public long getSchedule_id() {
		return schedule_id;
	}



	public void setSchedule_id(long schedule_id) {
		this.schedule_id = schedule_id;
	}



	public Vehicles getVehicle() {
		return vehicle;
	}



	public void setVehicle(Vehicles vehicle) {
		this.vehicle = vehicle;
	}



	public String getSchedule_name() {
		return schedule_name;
	}



	public void setSchedule_name(String schedule_name) {
		this.schedule_name = schedule_name;
	}



	public LocalDate getCollection_date() {
		return collection_date;
	}



	public void setCollection_date(LocalDate collection_date) {
		this.collection_date = collection_date;
	}



	public LocalTime getStart_time() {
		return start_time;
	}



	public void setStart_time(LocalTime start_time) {
		this.start_time = start_time;
	}



	public LocalTime getEnd_time() {
		return end_time;
	}



	public void setEnd_time(LocalTime end_time) {
		this.end_time = end_time;
	}



	public ScheduleStatus getStatus() {
		return status;
	}



	public void setStatus(ScheduleStatus status) {
		this.status = status;
	}



	public LocalDateTime getCreated_at() {
		return created_at;
	}



	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}



	public Set<Collection_Schedule_Days> getDays() {
		return days;
	}



	public void setDays(Set<Collection_Schedule_Days> days) {
		this.days = days;
	}



	public Set<Collection_Crew_Members> getCollection_Crew_Member() {
		return Collection_Crew_Member;
	}



	public void setCollection_Crew_Member(Set<Collection_Crew_Members> collection_Crew_Member) {
		Collection_Crew_Member = collection_Crew_Member;
	}



	public Set<Collection_Records> getCollection_Record() {
		return Collection_Record;
	}



	public void setCollection_Record(Set<Collection_Records> collection_Record) {
		Collection_Record = collection_Record;
	}



	public Area getAreas() {
		return areas;
	}



	public void setAreas(Area areas) {
		this.areas = areas;
	}



	public Collection_Schedules(long schedule_id, Vehicles vehicle, String schedule_name, LocalDate collection_date,
			LocalTime start_time, LocalTime end_time, ScheduleStatus status, LocalDateTime created_at,
			Set<Collection_Schedule_Days> days, Set<Collection_Crew_Members> collection_Crew_Member,
			Set<Collection_Records> collection_Record, Area areas) {
		super();
		this.schedule_id = schedule_id;
		this.vehicle = vehicle;
		this.schedule_name = schedule_name;
		this.collection_date = collection_date;
		this.start_time = start_time;
		this.end_time = end_time;
		this.status = status;
		this.created_at = created_at;
		this.days = days;
		Collection_Crew_Member = collection_Crew_Member;
		Collection_Record = collection_Record;
		this.areas = areas;
	}



	public Collection_Schedules() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
