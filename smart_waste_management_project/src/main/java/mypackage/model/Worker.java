package mypackage.model;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name="worker")
public class Worker {

	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int worker_id;
	
	@Column(name="joining_date",nullable=true)
	private  LocalDate  joining_date;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "availability_status", length = 20, nullable = false)
	private WorkerStatus  availability_status;
	
	@OneToOne()
	@JoinColumn(name="user_id",nullable=false,unique = true)
	private User user;
	
	@OneToMany(mappedBy = "workes")
	@JsonIgnoreProperties("workes")
	private Set<Collection_Crew_Members>Collection_Crew_Member;
	
	@OneToMany(mappedBy = "workes")
	@JsonIgnoreProperties("workes")
   private Set<Collection_Records>Collection_Record;

}
