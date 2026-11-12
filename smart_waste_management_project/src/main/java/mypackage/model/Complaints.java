package mypackage.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="complaints")
public class Complaints {
@Id
@GeneratedValue(strategy =GenerationType.IDENTITY )
@
private int complaint_id;

}
