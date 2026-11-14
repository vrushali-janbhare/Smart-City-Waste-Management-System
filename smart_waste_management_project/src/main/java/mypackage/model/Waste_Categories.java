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
@Table(name="waste_categories")
public class Waste_Categories {
@Id
@GeneratedValue(strategy =GenerationType.IDENTITY)
private long category_id;
@Column(name="category_name",unique=true,nullable=false,length=50)
private String category_name;

@Column(name="description",nullable=true,length=255)
private String description;

@Column(name="waste_type",nullable=false,length=20)
@Enumerated(EnumType.STRING)
private WasteType waste_type;

@Column(name="is_recyclable",nullable=false)
private boolean is_recyclable=false;

@Column(name="created_at",nullable=false)
private LocalDateTime created_at;

@OneToMany(mappedBy = "waste_categorie",fetch =FetchType.LAZY)
@JsonIgnoreProperties("waste_categorie")
private Set<Complaints>complaint;

public long getCategory_id() {
	return category_id;
}

public void setCategory_id(long category_id) {
	this.category_id = category_id;
}

public String getCategory_name() {
	return category_name;
}

public void setCategory_name(String category_name) {
	this.category_name = category_name;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public WasteType getWaste_type() {
	return waste_type;
}

public void setWaste_type(WasteType waste_type) {
	this.waste_type = waste_type;
}

public boolean isIs_recyclable() {
	return is_recyclable;
}

public void setIs_recyclable(boolean is_recyclable) {
	this.is_recyclable = is_recyclable;
}

public LocalDateTime getCreated_at() {
	return created_at;
}

public void setCreated_at(LocalDateTime created_at) {
	this.created_at = created_at;
}

public Set<Complaints> getComplaint() {
	return complaint;
}

public void setComplaint(Set<Complaints> complaint) {
	this.complaint = complaint;
}

public Waste_Categories(long category_id, String category_name, String description, WasteType waste_type,
		boolean is_recyclable, LocalDateTime created_at, Set<Complaints> complaint) {
	super();
	this.category_id = category_id;
	this.category_name = category_name;
	this.description = description;
	this.waste_type = waste_type;
	this.is_recyclable = is_recyclable;
	this.created_at = created_at;
	this.complaint = complaint;
}

public Waste_Categories() {
	super();
	// TODO Auto-generated constructor stub
} 

}
