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
@Table(name="workers")
public class Worker {

	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long worker_id;
	
	@Column(name="joining_date",nullable=true)
	private  LocalDate  joining_date;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "availability_status", length = 20, nullable = false)
	private WorkerStatus  availability_status;
	
	@OneToOne()
	@JoinColumn(name="user_id",nullable=false,unique = true)
	private User users;
	
	@OneToMany(mappedBy = "workers")
	@JsonIgnoreProperties("workers")
	private Set<Collection_Crew_Members>Collection_Crew_Member;
	
	@OneToMany(mappedBy = "workers")
	@JsonIgnoreProperties("workers")
	private Set<Complaints>complaint;
	
	@OneToMany(mappedBy = "workers")
	@JsonIgnoreProperties("workers")
   private Set<Collection_Records>Collection_Record;

	public long getWorker_id() {
		return worker_id;
	}

	public void setWorker_id(long worker_id) {
		this.worker_id = worker_id;
	}

	public LocalDate getJoining_date() {
		return joining_date;
	}

	public void setJoining_date(LocalDate joining_date) {
		this.joining_date = joining_date;
	}

	public WorkerStatus getAvailability_status() {
		return availability_status;
	}

	public void setAvailability_status(WorkerStatus availability_status) {
		this.availability_status = availability_status;
	}

	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

	public Set<Collection_Crew_Members> getCollection_Crew_Member() {
		return Collection_Crew_Member;
	}

	public void setCollection_Crew_Member(Set<Collection_Crew_Members> collection_Crew_Member) {
		Collection_Crew_Member = collection_Crew_Member;
	}

	public Set<Complaints> getComplaint() {
		return complaint;
	}

	public void setComplaint(Set<Complaints> complaint) {
		this.complaint = complaint;
	}

	public Set<Collection_Records> getCollection_Record() {
		return Collection_Record;
	}

	public void setCollection_Record(Set<Collection_Records> collection_Record) {
		Collection_Record = collection_Record;
	}

	public Worker(long worker_id, LocalDate joining_date, WorkerStatus availability_status, User users,
			Set<Collection_Crew_Members> collection_Crew_Member, Set<Complaints> complaint,
			Set<Collection_Records> collection_Record) {
		super();
		this.worker_id = worker_id;
		this.joining_date = joining_date;
		this.availability_status = availability_status;
		this.users = users;
		Collection_Crew_Member = collection_Crew_Member;
		this.complaint = complaint;
		Collection_Record = collection_Record;
	}

	public Worker() {
		super();
		// TODO Auto-generated constructor stub
	}
   

}
