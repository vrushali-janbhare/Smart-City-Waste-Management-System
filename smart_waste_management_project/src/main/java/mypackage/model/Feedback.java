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
@Table(name="feedback")
public class Feedback {

//	14. feedback table
//	Column	Data Type	Constraint
//	feedback_id	BIGINT	PK, AUTO_INCREMENT
//	user_id	BIGINT	FK → users(user_id), NOT NULL
//	complaint_id	BIGINT	FK → complaints(complaint_id), NULL
//	rating	INT	NOT NULL
//	comments	VARCHAR(500)	NULL
//	created_at	DATETIME	NOT NULL
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long feedback_id;
	
	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="user_id",nullable=false)
	private User users;
	
	@ManyToOne(fetch =FetchType.LAZY,optional=true)
	@JoinColumn(name="complaint_id",nullable=true)
	private Complaints complaint;
	
	@Column(name="rating",nullable=false)
	private int rating;
	
	@Column(name="comments",nullable=true,length = 500)
	private String comments;
	
	@Column(name="created_at",nullable=false)
	private LocalDateTime created_at;

	public long getFeedback_id() {
		return feedback_id;
	}

	public void setFeedback_id(long feedback_id) {
		this.feedback_id = feedback_id;
	}

	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

	public Complaints getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaints complaint) {
		this.complaint = complaint;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public Feedback(long feedback_id, User users, Complaints complaint, int rating, String comments,
			LocalDateTime created_at) {
		super();
		this.feedback_id = feedback_id;
		this.users = users;
		this.complaint = complaint;
		this.rating = rating;
		this.comments = comments;
		this.created_at = created_at;
	}

	public Feedback() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
