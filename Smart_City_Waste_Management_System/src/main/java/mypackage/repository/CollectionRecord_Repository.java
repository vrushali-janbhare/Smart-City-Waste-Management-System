
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.CollectionRecord;

public interface CollectionRecord_Repository extends JpaRepository<CollectionRecord, Long> {

}
