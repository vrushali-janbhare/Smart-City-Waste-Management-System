package mypackage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name="fines")
public class Fines {

//	16. fines table
//	Column	Data Type	Constraint
//	fine_id	BIGINT	PK, AUTO_INCREMENT
//	user_id	BIGINT	FK → users(user_id),  NULL
//	person_name VARCHAR(100)  not NULL
//	complaint_id	BIGINT	FK → complaints(complaint_id), NULL
//	fine_amount	DECIMAL(10,2)	NOT NULL
//	reason	VARCHAR(255)	NOT NULL
//	status	VARCHAR(20)	NOT NULL
//	issued_at	DATETIME	NOT NULL
//	paid_at	DATETIME	NULL
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fine_id;
	
	@ManyToOne(fetch =FetchType.LAZY,optional=true)
	@JoinColumn(name="user_id",nullable=true)
	private User users;
	
	@Column(name="person_name",length=100,nullable=false)
	private String person_name ;
	
	@ManyToOne(fetch  =FetchType.LAZY,optional=true)
	@JoinColumn(name = "complaint_id", nullable = true)
	private Complaints complaint;
	
	@Column(name="fine_amount",nullable=false)
	private BigDecimal fine_amount;
	
	@Column(name="reason",nullable=false,length=255)
	private String reason;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false,length=20)
	private FineStatus status;
	
	@Column(name="issued_at",nullable=false)
	private LocalDateTime issued_at;
	
	@Column(name="paid_at",nullable=true)
	private LocalDateTime paid_at;

	@OneToMany(mappedBy = "Fine",fetch = FetchType.LAZY)
	@JsonIgnoreProperties("Fine")
	private Set<Payments>Payment;

	public long getFine_id() {
		return fine_id;
	}

	public void setFine_id(long fine_id) {
		this.fine_id = fine_id;
	}

	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

	public String getPerson_name() {
		return person_name;
	}

	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}

	public Complaints getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaints complaint) {
		this.complaint = complaint;
	}

	public BigDecimal getFine_amount() {
		return fine_amount;
	}

	public void setFine_amount(BigDecimal fine_amount) {
		this.fine_amount = fine_amount;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public FineStatus getStatus() {
		return status;
	}

	public void setStatus(FineStatus status) {
		this.status = status;
	}

	public LocalDateTime getIssued_at() {
		return issued_at;
	}

	public void setIssued_at(LocalDateTime issued_at) {
		this.issued_at = issued_at;
	}

	public LocalDateTime getPaid_at() {
		return paid_at;
	}

	public void setPaid_at(LocalDateTime paid_at) {
		this.paid_at = paid_at;
	}

	public Set<Payments> getPayment() {
		return Payment;
	}

	public void setPayment(Set<Payments> payment) {
		Payment = payment;
	}

	public Fines(long fine_id, User users, String person_name, Complaints complaint, BigDecimal fine_amount,
			String reason, FineStatus status, LocalDateTime issued_at, LocalDateTime paid_at, Set<Payments> payment) {
		super();
		this.fine_id = fine_id;
		this.users = users;
		this.person_name = person_name;
		this.complaint = complaint;
		this.fine_amount = fine_amount;
		this.reason = reason;
		this.status = status;
		this.issued_at = issued_at;
		this.paid_at = paid_at;
		Payment = payment;
	}

	public Fines() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
