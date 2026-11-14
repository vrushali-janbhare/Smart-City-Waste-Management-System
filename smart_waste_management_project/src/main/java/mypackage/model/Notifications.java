package mypackage.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="notifications")
public class Notifications {
//	13. notifications table
//	Column	Data Type	Constraint
//	notification_id	BIGINT	PK, AUTO_INCREMENT
//	user_id	BIGINT	FK → users(user_id), NOT NULL
//	title	VARCHAR(100)	NOT NULL
//	message	VARCHAR(500)	NOT NULL
//	notification_type	VARCHAR(30)	NOT NULL
//	is_read	BOOLEAN	DEFAULT FALSE
//	created_at	DATETIME	NOT NULL

	
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private long notification_id;
	
	@ManyToOne(fetch =FetchType.LAZY,optional=false)
	@JoinColumn(name="user_id",nullable=false)
	private User users;
	
	@Column(name="title",nullable=false,length=100)
	private String title;
	
	@Column(name="message",nullable=false,length=500)
	private String message;
	
	@Enumerated(EnumType.STRING)
	@Column(name="notification_type",nullable=false,length=30)
	private notification_type  notification_types;
	
	@Column(name="is_read")
	private boolean is_read=false;
	
	
	@Column(name="created_at",nullable=false)
	private LocalDateTime created_at;


	public long getNotification_id() {
		return notification_id;
	}


	public void setNotification_id(long notification_id) {
		this.notification_id = notification_id;
	}


	public User getUsers() {
		return users;
	}


	public void setUsers(User users) {
		this.users = users;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public notification_type getNotification_types() {
		return notification_types;
	}


	public void setNotification_types(notification_type notification_types) {
		this.notification_types = notification_types;
	}


	public boolean isIs_read() {
		return is_read;
	}


	public void setIs_read(boolean is_read) {
		this.is_read = is_read;
	}


	public LocalDateTime getCreated_at() {
		return created_at;
	}


	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}


	public Notifications(long notification_id, User users, String title, String message,
			notification_type notification_types, boolean is_read, LocalDateTime created_at) {
		super();
		this.notification_id = notification_id;
		this.users = users;
		this.title = title;
		this.message = message;
		this.notification_types = notification_types;
		this.is_read = is_read;
		this.created_at = created_at;
	}


	public Notifications() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
