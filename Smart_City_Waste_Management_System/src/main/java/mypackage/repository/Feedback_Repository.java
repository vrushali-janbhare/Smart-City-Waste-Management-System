
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.Feedback;

public interface Feedback_Repository extends JpaRepository<Feedback, Long> {

}
