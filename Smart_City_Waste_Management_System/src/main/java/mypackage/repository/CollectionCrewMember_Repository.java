
package mypackage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mypackage.model.CollectionCrewMember;

@Repository
public interface CollectionCrewMember_Repository
        extends JpaRepository<CollectionCrewMember, Long> {

    // ==========================================
    // EXISTING METHODS
    // ==========================================

    // Get crew members of a particular schedule
    List<CollectionCrewMember> findByScheduleScheduleId(
            Long scheduleId
    );

    // Delete crew members of a particular schedule
    void deleteByScheduleScheduleId(Long scheduleId);


    // ==========================================
    // NEW METHODS FOR WORKER DASHBOARD
    // ==========================================

    // 1. Get all schedule assignments for a worker
    List<CollectionCrewMember> findByWorker_WorkerId(
            Long workerId
    );

    // 2. Check whether a worker is assigned
    // to a particular collection schedule
    boolean existsByWorker_WorkerIdAndSchedule_ScheduleId(
            Long workerId,
            Long scheduleId
    );
}
