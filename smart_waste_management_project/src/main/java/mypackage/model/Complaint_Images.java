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
	private long image_id;
	
	@Column(name="image_url",nullable=false,length=255)
	private String image_url;
	
	@Column(name="uploaded_at",nullable=false)
	private LocalDateTime uploaded_at;
	
	@ManyToOne(fetch  =FetchType.LAZY,optional=false)
	@JoinColumn(name = "complaint_id", nullable = false)
	private Complaints complaint;

	public long getImage_id() {
		return image_id;
	}

	public void setImage_id(long image_id) {
		this.image_id = image_id;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public LocalDateTime getUploaded_at() {
		return uploaded_at;
	}

	public void setUploaded_at(LocalDateTime uploaded_at) {
		this.uploaded_at = uploaded_at;
	}

	public Complaints getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaints complaint) {
		this.complaint = complaint;
	}

	public Complaint_Images(long image_id, String image_url, LocalDateTime uploaded_at, Complaints complaint) {
		super();
		this.image_id = image_id;
		this.image_url = image_url;
		this.uploaded_at = uploaded_at;
		this.complaint = complaint;
	}

	public Complaint_Images() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
