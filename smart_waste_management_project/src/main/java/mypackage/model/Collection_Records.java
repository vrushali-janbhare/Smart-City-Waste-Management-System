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
	private int record_id;
	
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

}
