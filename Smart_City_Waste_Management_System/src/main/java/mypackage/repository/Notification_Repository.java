
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mypackage.model.Notification;

public interface Notification_Repository extends JpaRepository<Notification, Long> {

}
