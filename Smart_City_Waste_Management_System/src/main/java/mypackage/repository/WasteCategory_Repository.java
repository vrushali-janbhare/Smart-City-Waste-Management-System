
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.WasteCategory;

public interface WasteCategory_Repository extends JpaRepository<WasteCategory, Long> {

}
