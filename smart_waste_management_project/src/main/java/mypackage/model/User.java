package mypackage.model;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="users")
public class User {
//	Column	Data Type	Constraint
//	user_id	BIGINT	PK, AUTO_INCREMENT
//	full_name	VARCHAR(100)	NOT NULL
//	email	VARCHAR(150)	UNIQUE, NOT NULL
//	phone	VARCHAR(15)	UNIQUE, NULL
//	password	VARCHAR(255)	NOT NULL
//	role	VARCHAR(20)	NOT NULL
//	area_id	BIGINT	FK → areas(area_id), NULL
//	address	VARCHAR(255)	NULL
//	is_active	BOOLEAN	DEFAULT TRUE
//	created_at	DATETIME	NOT NULL
//	updated_at	DATETIME	NULL
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
    private long user_id;
	@Column(name = "full_name", nullable = false,length=100)
	private String full_name;
    @Column(name = "email", nullable = false,length=150, unique = true)
	private String email;
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
    @Column(name = "phone", nullable = false, length=10)
	private String phone;
    @Column(name = "password", nullable = false, length=255)
	private String password;
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private UserRole role;
	
    @Column(name = "address", nullable = true )
	private String address;
    
    @Column(name = "is_active", nullable = false)
	private boolean  is_active=true;
    
    @Column(name = "created_at", nullable = false)
	private LocalDateTime created_at;
    
    @Column(name = "updated_at", nullable = true)
	private LocalDateTime updated_at;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=true)
	@JoinColumn(name="area_id",nullable = true)
	private Area areas;
	
	@OneToOne(mappedBy = "users")
	private Worker worker;
	
	@OneToMany(mappedBy = "users",fetch = FetchType.LAZY)
	@JsonIgnoreProperties("users")
	private Set<Complaints> complaints;
	
	@OneToMany(mappedBy="users",fetch = FetchType.LAZY)
	@JsonIgnoreProperties("users")
	private Set<Notifications>Notification;
	
	@OneToMany(mappedBy = "users",fetch = FetchType.LAZY)
	@JsonIgnoreProperties("users")
	private Set<Feedback>feedback;
	
	@OneToMany(mappedBy = "users",fetch = FetchType.LAZY)
	@JsonIgnoreProperties("users")
	private Set<Bulk_Pickup_Requests>Bulk_Pickup_Request;
	
	@OneToMany(mappedBy = "users",fetch = FetchType.LAZY)
	@JsonIgnoreProperties("users")
	private Set<Fines>Fines;
	
	@OneToMany(mappedBy = "users",fetch = FetchType.LAZY)
	@JsonIgnoreProperties("users")
	private Set<Complaint_Status_History>Complaint_Status_Historys;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isIs_active() {
		return is_active;
	}

	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public LocalDateTime getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(LocalDateTime updated_at) {
		this.updated_at = updated_at;
	}

	public Area getAreas() {
		return areas;
	}

	public void setAreas(Area areas) {
		this.areas = areas;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public Set<Complaints> getComplaints() {
		return complaints;
	}

	public void setComplaints(Set<Complaints> complaints) {
		this.complaints = complaints;
	}

	public Set<Notifications> getNotification() {
		return Notification;
	}

	public void setNotification(Set<Notifications> notification) {
		Notification = notification;
	}

	public Set<Feedback> getFeedback() {
		return feedback;
	}

	public void setFeedback(Set<Feedback> feedback) {
		this.feedback = feedback;
	}

	public Set<Bulk_Pickup_Requests> getBulk_Pickup_Request() {
		return Bulk_Pickup_Request;
	}

	public void setBulk_Pickup_Request(Set<Bulk_Pickup_Requests> bulk_Pickup_Request) {
		Bulk_Pickup_Request = bulk_Pickup_Request;
	}

	public Set<Fines> getFines() {
		return Fines;
	}

	public void setFines(Set<Fines> fines) {
		Fines = fines;
	}

	public Set<Complaint_Status_History> getComplaint_Status_Historys() {
		return Complaint_Status_Historys;
	}

	public void setComplaint_Status_Historys(Set<Complaint_Status_History> complaint_Status_Historys) {
		Complaint_Status_Historys = complaint_Status_Historys;
	}

	public User(long user_id, String full_name, String email,
			@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits") String phone,
			String password, UserRole role, String address, boolean is_active, LocalDateTime created_at,
			LocalDateTime updated_at, Area areas, Worker worker, Set<Complaints> complaints,
			Set<Notifications> notification, Set<Feedback> feedback, Set<Bulk_Pickup_Requests> bulk_Pickup_Request,
			Set<mypackage.model.Fines> fines, Set<Complaint_Status_History> complaint_Status_Historys) {
		super();
		this.user_id = user_id;
		this.full_name = full_name;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.role = role;
		this.address = address;
		this.is_active = is_active;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.areas = areas;
		this.worker = worker;
		this.complaints = complaints;
		Notification = notification;
		this.feedback = feedback;
		Bulk_Pickup_Request = bulk_Pickup_Request;
		Fines = fines;
		Complaint_Status_Historys = complaint_Status_Historys;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}