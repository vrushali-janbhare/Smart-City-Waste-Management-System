
package mypackage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mypackage.model.CollectionScheduleDay;

public interface CollectionScheduleDay_Repository
        extends JpaRepository<CollectionScheduleDay, Long> {

    List<CollectionScheduleDay> findByScheduleScheduleId(Long scheduleId);

    void deleteByScheduleScheduleId(Long scheduleId);
}
