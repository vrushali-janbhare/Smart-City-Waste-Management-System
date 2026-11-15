
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.AiClassification;

public interface AiClassification_Repository extends JpaRepository<AiClassification, Long> {

}
