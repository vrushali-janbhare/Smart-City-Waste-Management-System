
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.Payment;

public interface Payment_Repository extends JpaRepository<Payment, Long> {

}
