package mypackage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mypackage.model.*;
public interface Complaints_Repository extends JpaRepository<Complaints, Long> {

}
