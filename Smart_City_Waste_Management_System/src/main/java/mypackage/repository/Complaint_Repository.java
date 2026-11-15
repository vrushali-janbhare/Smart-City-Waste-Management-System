
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.Complaint;

public interface Complaint_Repository extends JpaRepository<Complaint, Long> {

}
