
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.Worker;

@Repository
public interface Worker_Repository extends JpaRepository<Worker, Long> {

    // Check whether an employee code already exists
    boolean existsByEmployeeCode(String employeeCode);

}
