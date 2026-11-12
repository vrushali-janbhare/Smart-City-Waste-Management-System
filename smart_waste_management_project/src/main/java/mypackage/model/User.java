package mypackage.model;

import java.time.LocalDateTime;

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
@Table(name="User")
public class User {
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
    private int user_id;
	private String user_name;
	private String email;
	private String phone;
	private String password;
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	private String address;
	private boolean  is_active;
	private LocalDateTime create_at;
	private LocalDateTime updated_at;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	@JoinColumn(name="area_id")
	private Area area;
}