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
private int complaint_id;

@Column(name="title",length=50,nullable=false)
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

@Column(name="priority",length=25,nullable=false)
private String  priority;

@Column(name="created_at",nullable=false)
private   LocalDateTime created_at;

@Column(name="updated_at",nullable=true)
private LocalDateTime updated_at;

@Column(name="resolved_at",nullable=true)
private LocalDateTime resolved_at;

@ManyToOne(fetch =FetchType.LAZY,optional=false)
@JoinColumn(name="user_id",nullable=false)
private User user;

@ManyToOne(fetch =FetchType.LAZY,optional=false)
@JoinColumn(name="area_id",nullable=false)
private Area area;

@ManyToOne(fetch =FetchType.LAZY,optional=false)
@JoinColumn(name="category_id",nullable=false)
private Waste_Categories waste_categorie;

@ManyToOne(fetch =FetchType.LAZY,optional=false)
@JoinColumn(name="worker_id",nullable=false)
private Worker worker;

@OneToMany(mappedBy ="complaint", fetch = FetchType.LAZY)
@JsonIgnoreProperties("complaint")
private Set<Complaint_Images>complaintImages;
}
