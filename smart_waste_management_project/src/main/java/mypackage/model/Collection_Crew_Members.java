package mypackage.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="collection_crew_members")
public class Collection_Crew_Members {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long crew_member_id;
	
	@ManyToOne(fetch =FetchType.LAZY, optional = false)
	@JoinColumn(name="schedule_id",nullable=false)
	private Collection_Schedules  collection_schedule;
	
	@ManyToOne(fetch =FetchType.LAZY, optional = false)
	@JoinColumn(name="worker_id",nullable=false)
	private Worker workers;

	@Column(name="assigned_at",nullable=false)
	private LocalDateTime assigned_at;

	public long getCrew_member_id() {
		return crew_member_id;
	}

	public void setCrew_member_id(long crew_member_id) {
		this.crew_member_id = crew_member_id;
	}

	public Collection_Schedules getCollection_schedule() {
		return collection_schedule;
	}

	public void setCollection_schedule(Collection_Schedules collection_schedule) {
		this.collection_schedule = collection_schedule;
	}

	public Worker getWorkers() {
		return workers;
	}

	public void setWorkers(Worker workers) {
		this.workers = workers;
	}

	public LocalDateTime getAssigned_at() {
		return assigned_at;
	}

	public void setAssigned_at(LocalDateTime assigned_at) {
		this.assigned_at = assigned_at;
	}

	public Collection_Crew_Members(long crew_member_id, Collection_Schedules collection_schedule, Worker workers,
			LocalDateTime assigned_at) {
		super();
		this.crew_member_id = crew_member_id;
		this.collection_schedule = collection_schedule;
		this.workers = workers;
		this.assigned_at = assigned_at;
	}

	public Collection_Crew_Members() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
