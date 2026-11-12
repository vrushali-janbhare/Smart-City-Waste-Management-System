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
@Table(name="Waste_Categories")
public class Waste_Categories {
@Id
@GeneratedValue(strategy =GenerationType.IDENTITY)
private int category_id;
@Column(name="category_name",unique=true,nullable=false)
private String category_name;
@Column(name="description",nullable=true)
private String description;
@Column(name="waste_type",nullable=false)
@Enumerated(EnumType.STRING)
private WasteType waste_type;
@Column(name="is_recyclable")
private boolean is_recyclable=false;
@Column(name="created_at",nullable=false)
private LocalDateTime created_at;
@OneToMany(mappedBy = "waste_categories",fetch =FetchType.LAZY)
@JsonIgnoreProperties("waste_categories")
private Set<Complaint>Complaints; 




}
