
package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.ContactMessage;

@Repository
public interface ContactMessage_Repository
        extends JpaRepository<ContactMessage, Long> {

}
