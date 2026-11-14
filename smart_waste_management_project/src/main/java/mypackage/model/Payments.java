package mypackage.model;

import java.math.BigDecimal;
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
@Table(name="payments")
public class Payments {

//	17. payments table
//	Column	Data Type	Constraint
//	payment_id	BIGINT	PK, AUTO_INCREMENT
//	fine_id	BIGINT	FK → fines(fine_id), NOT NULL
//	amount	DECIMAL(10,2)	NOT NULL
//	payment_method	VARCHAR(30)	NOT NULL
//	transaction_id	VARCHAR(100)	UNIQUE, NULL
//	status	VARCHAR(20)	NOT NULL
//	paid_at	DATETIME	NULL
//	created_at	DATETIME	NOT NULL
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long  payment_id;
	
	@ManyToOne(fetch  =FetchType.LAZY,optional=false)
	@JoinColumn(name = "fine_id", nullable = false)
	private Fines Fine;
	
	@Column(name="amount",nullable=false)
	private  BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method",nullable = false,length=30)
	private PaymentMethod  payment_method;
	
	@Column(name="transaction_id",unique =true,nullable=true,length = 100)
	private String transaction_id;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status",nullable=false,length=20)
	private PaymentStatus  status;
	
	@Column(name="paid_at",nullable = true)
	private LocalDateTime paid_at;
	
	@Column(name="created_at",nullable = false)
	private LocalDateTime created_at;

	public long getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(long payment_id) {
		this.payment_id = payment_id;
	}

	public Fines getFine() {
		return Fine;
	}

	public void setFine(Fines fine) {
		Fine = fine;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentMethod getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(PaymentMethod payment_method) {
		this.payment_method = payment_method;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public LocalDateTime getPaid_at() {
		return paid_at;
	}

	public void setPaid_at(LocalDateTime paid_at) {
		this.paid_at = paid_at;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public Payments(long payment_id, Fines fine, BigDecimal amount, PaymentMethod payment_method, String transaction_id,
			PaymentStatus status, LocalDateTime paid_at, LocalDateTime created_at) {
		super();
		this.payment_id = payment_id;
		Fine = fine;
		this.amount = amount;
		this.payment_method = payment_method;
		this.transaction_id = transaction_id;
		this.status = status;
		this.paid_at = paid_at;
		this.created_at = created_at;
	}

	public Payments() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
