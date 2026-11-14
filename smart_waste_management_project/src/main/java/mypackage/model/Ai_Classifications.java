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
@Table(name="ai_classifications")
public class Ai_Classifications {

	
//	18. ai_classifications table
//	Column	Data Type	Constraint
//	classification_id	BIGINT	PK, AUTO_INCREMENT
//	complaint_id	BIGINT	FK → complaints(complaint_id), NOT NULL
//	predicted_category	VARCHAR(50)	NOT NULL
//	confidence_score	DOUBLE	NOT NULL
//	image_url	VARCHAR(255)	NULL
//	model_name	VARCHAR(100)	NULL
//	classified_at	DATETIME	NOT NULL
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   private long classification_id;
	
	@ManyToOne(fetch  =FetchType.LAZY,optional=false)
	@JoinColumn(name = "complaint_id", nullable = false)
	private Complaints complaint;
	
	@Column(name="predicted_category",nullable=false,length=50)
	private String predicted_category;
	
	@Column(name="confidence_score",nullable=false)
	private Double confidence_score;
	
	@Column(name="image_url",nullable=true,length=255)
	private String image_url;
	
	@Column(name = "model_name",nullable =true,length =100)
	private String model_name;
	
	@Column(name="classified_at",nullable=false)
	private LocalDateTime classified_at;

	public long getClassification_id() {
		return classification_id;
	}

	public void setClassification_id(long classification_id) {
		this.classification_id = classification_id;
	}

	public Complaints getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaints complaint) {
		this.complaint = complaint;
	}

	public String getPredicted_category() {
		return predicted_category;
	}

	public void setPredicted_category(String predicted_category) {
		this.predicted_category = predicted_category;
	}

	public Double getConfidence_score() {
		return confidence_score;
	}

	public void setConfidence_score(Double confidence_score) {
		this.confidence_score = confidence_score;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public LocalDateTime getClassified_at() {
		return classified_at;
	}

	public void setClassified_at(LocalDateTime classified_at) {
		this.classified_at = classified_at;
	}

	public Ai_Classifications(long classification_id, Complaints complaint, String predicted_category,
			Double confidence_score, String image_url, String model_name, LocalDateTime classified_at) {
		super();
		this.classification_id = classification_id;
		this.complaint = complaint;
		this.predicted_category = predicted_category;
		this.confidence_score = confidence_score;
		this.image_url = image_url;
		this.model_name = model_name;
		this.classified_at = classified_at;
	}

	public Ai_Classifications() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
