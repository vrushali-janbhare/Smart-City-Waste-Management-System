package mypackage.model;

import java.time.LocalDate;
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
@Table(name="collection_records")
public class Collection_Records {

//	Column	Data Type	Constraint
//	record_id	BIGINT	PK, AUTO_INCREMENT
//	schedule_id	BIGINT	FK → collection_schedules(schedule_id), NOT NULL
//	worker_id	BIGINT	FK → workers(worker_id), NOT NULL
//	collection_date	DATE	NOT NULL
//	collected_quantity	DOUBLE	NOT NULL
//	remarks	VARCHAR(255)	NULL
//	created_at	DATETIME	NOT NULL
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long record_id;
	
	@Column(name="collection_date",nullable=false)
	private LocalDate collection_date;
	
	@Column(name="collected_quantity",nullable=false)
	private Double collected_quantity;
	
	@Column(name="remarks",nullable=true,length=255)
	private String remarks;
	
	@Column(name="created_at",nullable=false)
	private LocalDateTime created_at;
	
	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="schedule_id",nullable=false)
	private Collection_Schedules  collection_schedule;
	
	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="worker_id",nullable=false)
	private Worker workers;

	public long getRecord_id() {
		return record_id;
	}

	public void setRecord_id(long record_id) {
		this.record_id = record_id;
	}

	public LocalDate getCollection_date() {
		return collection_date;
	}

	public void setCollection_date(LocalDate collection_date) {
		this.collection_date = collection_date;
	}

	public Double getCollected_quantity() {
		return collected_quantity;
	}

	public void setCollected_quantity(Double collected_quantity) {
		this.collected_quantity = collected_quantity;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
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

	public Collection_Records(long record_id, LocalDate collection_date, Double collected_quantity, String remarks,
			LocalDateTime created_at, Collection_Schedules collection_schedule, Worker workers) {
		super();
		this.record_id = record_id;
		this.collection_date = collection_date;
		this.collected_quantity = collected_quantity;
		this.remarks = remarks;
		this.created_at = created_at;
		this.collection_schedule = collection_schedule;
		this.workers = workers;
	}

	public Collection_Records() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
