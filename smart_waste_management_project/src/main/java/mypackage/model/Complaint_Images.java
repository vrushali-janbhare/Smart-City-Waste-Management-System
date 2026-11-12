package mypackage.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity
@Table(name="complaint_images")
public class Complaint_Images {	
//	Column	Data Type	Constraint
//	image_id	BIGINT	PK, AUTO_INCREMENT
//	complaint_id	BIGINT	FK → complaints(complaint_id), NOT NULL
//	image_url	VARCHAR(255)	NOT NULL
//	uploaded_at	DATETIME	NOT NULL
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int image_id;
	
	@Column(name="image_url",nullable=false,length=255)
	private String image_url;
	
	@Column(name="uploaded_at",nullable=false)
	private LocalDateTime uploaded_at;
	
	@ManyToOne(fetch  =FetchType.LAZY,optional=false)
	@JoinColumn(name = "complaint_id", nullable = false)
	private Complaints complaint;
}
