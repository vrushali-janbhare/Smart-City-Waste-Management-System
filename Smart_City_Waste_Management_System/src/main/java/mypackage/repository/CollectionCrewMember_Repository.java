
package mypackage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mypackage.model.CollectionCrewMember;

public interface CollectionCrewMember_Repository
        extends JpaRepository<CollectionCrewMember, Long> {

    List<CollectionCrewMember> findByScheduleScheduleId(Long scheduleId);

    void deleteByScheduleScheduleId(Long scheduleId);
}
