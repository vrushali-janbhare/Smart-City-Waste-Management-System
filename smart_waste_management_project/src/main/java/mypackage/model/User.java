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
}