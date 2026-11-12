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
@Table(name="Area")
public class Area {


@Id
@GeneratedValue(strategy =GenerationType.IDENTITY)
@Column(name="area_id")

private int area_id;
private String area_name;
private String ward_name;
private String city_name;
private String pincode;
private String description;
private LocalDateTime createdAt;

@OneToMany(mappedBy = "area",
fetch = FetchType.LAZY,
cascade = CascadeType.ALL)
@JsonIgnoreProperties("area")
private Set<User>users;
}