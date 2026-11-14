package mypackage.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name="complaints")
public class Complaints{
@Id
@GeneratedValue(strategy =GenerationType.IDENTITY )
private long complaint_id;

@Column(name="title",length=150,nullable=false)
private String title;

@Column(name="description",columnDefinition = "TEXT", nullable = false)
private String description;

@Column(name="location_address",length=255,nullable=false)
private String location_address;

@Column(name="latitude",nullable=true)
private Double  latitude;

@Column(name="longitude",nullable=true)
private Double longitude;

@Column(name="status",length=30,nullable=false)
private String status;

@Column(name="priority",length=20,nullable=false)
private String  priority;

@Column(name="created_at",nullable=false)
private   LocalDateTime created_at;

@Column(name="updated_at",nullable=true)
private LocalDateTime updated_at;

@Column(name="resolved_at",nullable=true)
private LocalDateTime resolved_at;

@ManyToOne(fetch =FetchType.LAZY,optional=false)
@JoinColumn(name="user_id",nullable=false)
private User users;

@ManyToOne(fetch =FetchType.LAZY,optional=false)
@JoinColumn(name="area_id",nullable=false)
private Area areas;

@ManyToOne(fetch =FetchType.LAZY,optional=false)
@JoinColumn(name="category_id",nullable=false)
private Waste_Categories waste_categorie;

@ManyToOne(fetch =FetchType.LAZY,optional=true)
@JoinColumn(name="assigned_worker_id",nullable=true)
private Worker workers;

@OneToMany(mappedBy ="complaint", fetch = FetchType.LAZY)
@JsonIgnoreProperties("complaint")
private Set<Complaint_Images>complaintImages;

@OneToMany(mappedBy="complaint",fetch = FetchType.LAZY)
@JsonIgnoreProperties("complaint")
private Set<Complaint_Status_History>Complaint_Status_Historys;

@OneToMany(mappedBy="complaint",fetch = FetchType.LAZY)
@JsonIgnoreProperties("complaint")
private Set<Feedback>feedback;

@OneToMany(mappedBy="complaint",fetch = FetchType.LAZY)
@JsonIgnoreProperties("complaint")
private Set<Fines>Fine;


@OneToMany(mappedBy="complaint",fetch = FetchType.LAZY)
@JsonIgnoreProperties("complaint")
private Set<Ai_Classifications>Ai_Classification;


public long getComplaint_id() {
	return complaint_id;
}


public void setComplaint_id(long complaint_id) {
	this.complaint_id = complaint_id;
}


public String getTitle() {
	return title;
}


public void setTitle(String title) {
	this.title = title;
}


public String getDescription() {
	return description;
}


public void setDescription(String description) {
	this.description = description;
}


public String getLocation_address() {
	return location_address;
}


public void setLocation_address(String location_address) {
	this.location_address = location_address;
}


public Double getLatitude() {
	return latitude;
}


public void setLatitude(Double latitude) {
	this.latitude = latitude;
}


public Double getLongitude() {
	return longitude;
}


public void setLongitude(Double longitude) {
	this.longitude = longitude;
}


public String getStatus() {
	return status;
}


public void setStatus(String status) {
	this.status = status;
}


public String getPriority() {
	return priority;
}


public void setPriority(String priority) {
	this.priority = priority;
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


public LocalDateTime getResolved_at() {
	return resolved_at;
}


public void setResolved_at(LocalDateTime resolved_at) {
	this.resolved_at = resolved_at;
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


public Waste_Categories getWaste_categorie() {
	return waste_categorie;
}


public void setWaste_categorie(Waste_Categories waste_categorie) {
	this.waste_categorie = waste_categorie;
}


public Worker getWorkers() {
	return workers;
}


public void setWorkers(Worker workers) {
	this.workers = workers;
}


public Set<Complaint_Images> getComplaintImages() {
	return complaintImages;
}


public void setComplaintImages(Set<Complaint_Images> complaintImages) {
	this.complaintImages = complaintImages;
}


public Set<Complaint_Status_History> getComplaint_Status_Historys() {
	return Complaint_Status_Historys;
}


public void setComplaint_Status_Historys(Set<Complaint_Status_History> complaint_Status_Historys) {
	Complaint_Status_Historys = complaint_Status_Historys;
}


public Set<Feedback> getFeedback() {
	return feedback;
}


public void setFeedback(Set<Feedback> feedback) {
	this.feedback = feedback;
}


public Set<Fines> getFine() {
	return Fine;
}


public void setFine(Set<Fines> fine) {
	Fine = fine;
}


public Set<Ai_Classifications> getAi_Classification() {
	return Ai_Classification;
}


public void setAi_Classification(Set<Ai_Classifications> ai_Classification) {
	Ai_Classification = ai_Classification;
}


public Complaints(long complaint_id, String title, String description, String location_address, Double latitude,
		Double longitude, String status, String priority, LocalDateTime created_at, LocalDateTime updated_at,
		LocalDateTime resolved_at, User users, Area areas, Waste_Categories waste_categorie, Worker workers,
		Set<Complaint_Images> complaintImages, Set<Complaint_Status_History> complaint_Status_Historys,
		Set<Feedback> feedback, Set<Fines> fine, Set<Ai_Classifications> ai_Classification) {
	super();
	this.complaint_id = complaint_id;
	this.title = title;
	this.description = description;
	this.location_address = location_address;
	this.latitude = latitude;
	this.longitude = longitude;
	this.status = status;
	this.priority = priority;
	this.created_at = created_at;
	this.updated_at = updated_at;
	this.resolved_at = resolved_at;
	this.users = users;
	this.areas = areas;
	this.waste_categorie = waste_categorie;
	this.workers = workers;
	this.complaintImages = complaintImages;
	Complaint_Status_Historys = complaint_Status_Historys;
	this.feedback = feedback;
	Fine = fine;
	Ai_Classification = ai_Classification;
}


public Complaints() {
	super();
	// TODO Auto-generated constructor stub
}


}
