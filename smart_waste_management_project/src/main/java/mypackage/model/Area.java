package mypackage.model;

import javax.persistence.Column;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
@Entity
@Table(name="areas")
public class Area {

//	Column	Data Type	Constraint
//	area_id	BIGINT	PK, AUTO_INCREMENT
//	area_name	VARCHAR(100)	NOT NULL
//	ward_number	VARCHAR(20)	NOT NULL
//	city_name	VARCHAR(100)	NOT NULL
//	pincode	VARCHAR(10)	NULL
//	description	VARCHAR(255)	NULL
//	created_at	DATETIME	NOT NULL

@Id
@GeneratedValue(strategy =GenerationType.IDENTITY)
@Column(name="area_id")
private long area_id;

@Column(name = "area_name",nullable=false,length = 100)
private String area_name;

@Column(name="ward_number",length =20,nullable = false)
private String ward_number;

@Column(name ="city_name",length = 100,nullable = false)
private String city_name;

@Column(name ="pincode",length = 10,nullable = true )
private String pincode;

@Column(name = "description",length = 255,nullable = true)
private String description;

@Column(name ="created_at",nullable =false)
private LocalDateTime created_At;

@OneToMany(mappedBy = "areas",fetch = FetchType.LAZY)
@JsonIgnoreProperties("areas")
private Set<Complaints>complaint;

@OneToMany(mappedBy = "areas",fetch = FetchType.LAZY)
@JsonIgnoreProperties("areas")
private Set<User>users;

@OneToMany(mappedBy = "areas",fetch = FetchType.LAZY)
@JsonIgnoreProperties("areas")
private Set<Bulk_Pickup_Requests>Bulk_Pickup_Request;

@OneToMany(mappedBy = "areas",fetch = FetchType.LAZY)
@JsonIgnoreProperties("areas")
private Set<Collection_Schedules>Collection_Schedule;

public long getArea_id() {
	return area_id;
}

public void setArea_id(long area_id) {
	this.area_id = area_id;
}

public String getArea_name() {
	return area_name;
}

public void setArea_name(String area_name) {
	this.area_name = area_name;
}

public String getWard_number() {
	return ward_number;
}

public void setWard_number(String ward_number) {
	this.ward_number = ward_number;
}

public String getCity_name() {
	return city_name;
}

public void setCity_name(String city_name) {
	this.city_name = city_name;
}

public String getPincode() {
	return pincode;
}

public void setPincode(String pincode) {
	this.pincode = pincode;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public LocalDateTime getCreated_At() {
	return created_At;
}

public void setCreated_At(LocalDateTime created_At) {
	this.created_At = created_At;
}

public Set<Complaints> getComplaint() {
	return complaint;
}

public void setComplaint(Set<Complaints> complaint) {
	this.complaint = complaint;
}

public Set<User> getUsers() {
	return users;
}

public void setUsers(Set<User> users) {
	this.users = users;
}

public Set<Bulk_Pickup_Requests> getBulk_Pickup_Request() {
	return Bulk_Pickup_Request;
}

public void setBulk_Pickup_Request(Set<Bulk_Pickup_Requests> bulk_Pickup_Request) {
	Bulk_Pickup_Request = bulk_Pickup_Request;
}

public Set<Collection_Schedules> getCollection_Schedule() {
	return Collection_Schedule;
}

public void setCollection_Schedule(Set<Collection_Schedules> collection_Schedule) {
	Collection_Schedule = collection_Schedule;
}

public Area(long area_id, String area_name, String ward_number, String city_name, String pincode, String description,
		LocalDateTime created_At, Set<Complaints> complaint, Set<User> users,
		Set<Bulk_Pickup_Requests> bulk_Pickup_Request, Set<Collection_Schedules> collection_Schedule) {
	super();
	this.area_id = area_id;
	this.area_name = area_name;
	this.ward_number = ward_number;
	this.city_name = city_name;
	this.pincode = pincode;
	this.description = description;
	this.created_At = created_At;
	this.complaint = complaint;
	this.users = users;
	Bulk_Pickup_Request = bulk_Pickup_Request;
	Collection_Schedule = collection_Schedule;
}

public Area() {
	super();
	// TODO Auto-generated constructor stub
}

}