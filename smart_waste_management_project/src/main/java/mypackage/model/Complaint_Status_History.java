package mypackage.model;

import java.time.LocalDateTime;

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
@Table(name="complaint_status_history")
public class Complaint_Status_History {

	
//	12. complaint_status_history table
//	Column	Data Type	Constraint
//	history_id	BIGINT	PK, AUTO_INCREMENT
//	complaint_id	BIGINT	FK → complaints(complaint_id), NOT NULL
//	status	VARCHAR(30)	NOT NULL
//	changed_by	BIGINT	FK → users(user_id), NOT NULL
//	remarks	VARCHAR(255)	NULL
//	changed_at	DATETIME	NOT NULL
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private long history_id;
	
	@Column(name="remarks",nullable=true,length=255)
	private String remarks;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 30, nullable = false)
	private  ComplaintStatus   status;
	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="complaint_id",nullable=false)
    private Complaints  complaint;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "changed_by", nullable = false)
	private User users;
	
	@Column(name="changed_at",nullable=false)
	private LocalDateTime changed_at;

	public long getHistory_id() {
		return history_id;
	}

	public void setHistory_id(long history_id) {
		this.history_id = history_id;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public ComplaintStatus getStatus() {
		return status;
	}

	public void setStatus(ComplaintStatus status) {
		this.status = status;
	}

	public Complaints getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaints complaint) {
		this.complaint = complaint;
	}

	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

	public LocalDateTime getChanged_at() {
		return changed_at;
	}

	public void setChanged_at(LocalDateTime changed_at) {
		this.changed_at = changed_at;
	}

	public Complaint_Status_History(long history_id, String remarks, ComplaintStatus status, Complaints complaint,
			User users, LocalDateTime changed_at) {
		super();
		this.history_id = history_id;
		this.remarks = remarks;
		this.status = status;
		this.complaint = complaint;
		this.users = users;
		this.changed_at = changed_at;
	}

	public Complaint_Status_History() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
