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
	private int vehicle_id;
	
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
	

}
