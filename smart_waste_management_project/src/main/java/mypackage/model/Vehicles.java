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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="vehicles")
public class Vehicles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long vehicle_id;
	
	@Column(name="vehicle_number",unique=true,nullable=false,length=20)
	private String vehicle_number;
	
	@Column(name="vehicle_type",length=50,nullable=false)
	private String vehicle_type;
	
	@Column(name="capacity",nullable=false)
	private Double capacity;
	@Enumerated(EnumType.STRING)
	@Column(name="status",nullable=false)
	private VehicleStatus  status;
	
	@Column(name="created_at",nullable=false)
	private LocalDateTime created_at;
	
	
	@OneToMany(mappedBy ="vehicle",fetch = FetchType.LAZY)
	@JsonIgnoreProperties("vehicle")
	private Set<Collection_Schedules>Collection_Schedule;


	public long getVehicle_id() {
		return vehicle_id;
	}


	public void setVehicle_id(long vehicle_id) {
		this.vehicle_id = vehicle_id;
	}


	public String getVehicle_number() {
		return vehicle_number;
	}


	public void setVehicle_number(String vehicle_number) {
		this.vehicle_number = vehicle_number;
	}


	public String getVehicle_type() {
		return vehicle_type;
	}


	public void setVehicle_type(String vehicle_type) {
		this.vehicle_type = vehicle_type;
	}


	public Double getCapacity() {
		return capacity;
	}


	public void setCapacity(Double capacity) {
		this.capacity = capacity;
	}


	public VehicleStatus getStatus() {
		return status;
	}


	public void setStatus(VehicleStatus status) {
		this.status = status;
	}


	public LocalDateTime getCreated_at() {
		return created_at;
	}


	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}


	public Set<Collection_Schedules> getCollection_Schedule() {
		return Collection_Schedule;
	}


	public void setCollection_Schedule(Set<Collection_Schedules> collection_Schedule) {
		Collection_Schedule = collection_Schedule;
	}


	public Vehicles(long vehicle_id, String vehicle_number, String vehicle_type, Double capacity, VehicleStatus status,
			LocalDateTime created_at, Set<Collection_Schedules> collection_Schedule) {
		super();
		this.vehicle_id = vehicle_id;
		this.vehicle_number = vehicle_number;
		this.vehicle_type = vehicle_type;
		this.capacity = capacity;
		this.status = status;
		this.created_at = created_at;
		Collection_Schedule = collection_Schedule;
	}


	public Vehicles() {
		super();
		// TODO Auto-generated constructor stub
	}
	
   
}
