package mypackage.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import mypackage.model.*;
public interface Complaint_Status_History_Repository extends JpaRepository<Complaint_Status_History, Long> {

}
