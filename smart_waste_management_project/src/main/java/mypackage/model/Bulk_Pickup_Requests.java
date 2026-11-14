package mypackage.model;

import java.time.LocalDate;
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
@Table(name="bulk_pickup_requests")
public class Bulk_Pickup_Requests {

//	15. bulk_pickup_requests table
//	Column	Data Type	Constraint
//	request_id	BIGINT	PK, AUTO_INCREMENT
//	user_id	BIGINT	FK → users(user_id), NOT NULL
//	area_id	BIGINT	FK → areas(area_id), NOT NULL
//	waste_description	VARCHAR(500)	NOT NULL
//	estimated_quantity	DOUBLE	NOT NULL
//	pickup_address	VARCHAR(255)	NOT NULL
//	requested_date	DATE	NOT NULL
//	status	VARCHAR(20)	NOT NULL
//	created_at	DATETIME	NOT NULL
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long request_id;
	
	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="user_id",nullable=false)
	private User users;
	
	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="area_id",nullable=false)
	private Area areas;
	
    @Column(name="waste_description",nullable=false,length=500)
    private String waste_description;
    
    @Column(name="estimated_quantity",nullable=false)
    private Double estimated_quantity;
    
    @Column(name="pickup_address",nullable=false)
    private String pickup_address;
    
    @Column(name="requested_date",nullable=false)
    private LocalDate requested_date;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=false,length=20)
    private BulkPickupStatus status;
	
	@Column(name="created_at",nullable=false)
	private LocalDateTime created_at;

	public long getRequest_id() {
		return request_id;
	}

	public void setRequest_id(long request_id) {
		this.request_id = request_id;
	}

	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

	public Area getAreas() {
		return areas;
	}

	public void setAreas(Area areas) {
		this.areas = areas;
	}

	public String getWaste_description() {
		return waste_description;
	}

	public void setWaste_description(String waste_description) {
		this.waste_description = waste_description;
	}

	public Double getEstimated_quantity() {
		return estimated_quantity;
	}

	public void setEstimated_quantity(Double estimated_quantity) {
		this.estimated_quantity = estimated_quantity;
	}

	public String getPickup_address() {
		return pickup_address;
	}

	public void setPickup_address(String pickup_address) {
		this.pickup_address = pickup_address;
	}

	public LocalDate getRequested_date() {
		return requested_date;
	}

	public void setRequested_date(LocalDate requested_date) {
		this.requested_date = requested_date;
	}

	public BulkPickupStatus getStatus() {
		return status;
	}

	public void setStatus(BulkPickupStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public Bulk_Pickup_Requests(long request_id, User users, Area areas, String waste_description,
			Double estimated_quantity, String pickup_address, LocalDate requested_date, BulkPickupStatus status,
			LocalDateTime created_at) {
		super();
		this.request_id = request_id;
		this.users = users;
		this.areas = areas;
		this.waste_description = waste_description;
		this.estimated_quantity = estimated_quantity;
		this.pickup_address = pickup_address;
		this.requested_date = requested_date;
		this.status = status;
		this.created_at = created_at;
	}

	public Bulk_Pickup_Requests() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
