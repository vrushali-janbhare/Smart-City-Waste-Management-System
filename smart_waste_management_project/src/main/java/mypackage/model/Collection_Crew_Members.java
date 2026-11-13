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
    private int crew_member_id;
	
	@ManyToOne(fetch =FetchType.LAZY, optional = false)
	@JoinColumn(name="schedule_id",nullable=false)
	private Collection_Schedules  collection_schedule;
	
	@ManyToOne(fetch =FetchType.LAZY, optional = false)
	@JoinColumn(name="worker_id",nullable=false)
	private Worker workers;

	@Column(name="assigned_at",nullable=false)
	private LocalDateTime assigned_at;
}
