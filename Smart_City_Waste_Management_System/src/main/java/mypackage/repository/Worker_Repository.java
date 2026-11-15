
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.Worker;

public interface Worker_Repository extends JpaRepository<Worker, Long> {

}
